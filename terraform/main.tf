terraform {
  backend "s3" {
    bucket = "terraform-state-emsp"
    key    = "env/dev/terraform.tfstate"
    region = "ap-northeast-1"
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.81.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true # fix creating RDS DB Instance (mysql): operation error RDS: CreateDBInstance, https response error StatusCode: 400, RequestID: ec0ff350-7262-4a32-86ac-d548f1efddf5, InvalidVPCNetworkStateFault: Cannot create a publicly accessible DBInstance.  The specified VPC does not support DNS resolution, DNS hostnames, or both. Update the VPC and then try again
  enable_dns_support = true
  tags = {
    Name = "main-vpc"
  }
}

resource "aws_subnet" "public" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "ap-northeast-1a"
  map_public_ip_on_launch = true
}

resource "aws_subnet" "publicB" { //  The DB subnet group doesn't meet Availability Zone (AZ) coverage requirement. Current AZ coverage: ap-northeast-1c. Add subnets to cover at least 2 AZs.
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.2.0/24"
  availability_zone = "ap-northeast-1c"
  map_public_ip_on_launch = true
}

resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id
}

resource "aws_route_table" "r" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }
}

resource "aws_route_table_association" "a" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.r.id
}

# load balance security_group
resource "aws_security_group" "alb_sg" {
  name        = "alb_sg"
  description = "Allow HTTP and HTTPS"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "emsp_sg" {
  name        = "emsp_sg"
  description = "Allow HTTP SSH"
  vpc_id = aws_vpc.main.id

  ingress {
      description = "HTTP"
      from_port   = 8080
      to_port     = 8080
      protocol    = "tcp"
      security_groups = [aws_security_group.alb_sg.id]
  }

  egress {
      description = "Allow all outbound traffic"
      from_port   = 0
      to_port     = 0
      protocol    = "-1"
      cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "rds_sg" {
  name = "rds_sg"
  description = "Allow Mysql access from Anywhere"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    #security_groups = [aws_security_group.emsp_sg.id]
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_subnet_group" "main" {
  name = "main"
  subnet_ids = [aws_subnet.public.id, aws_subnet.publicB.id]
}

resource "aws_db_instance" "mysql" {
  allocated_storage = 20
  engine = "mysql"
  engine_version = "8.0"
  db_name = var.db_name
  username = var.db_username
  password = var.db_password
  publicly_accessible = true
  skip_final_snapshot = true
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  db_subnet_group_name = aws_db_subnet_group.main.name
  instance_class = "db.t4g.micro"
  identifier = "mysql"
}

resource "aws_instance" "emsp" {
  count = var.instance_count

  ami = "ami-054400ced365b82a0" #Ubuntu Server 24.04 LTS (HVM)
  instance_type               = var.instance_type
  subnet_id                   = aws_subnet.public.id
  key_name                    = var.key_name
  security_groups             = [aws_security_group.emsp_sg.id]
  associate_public_ip_address = true

  user_data = templatefile("init-ecs.sh", {
    DB_HOST              = aws_db_instance.mysql.address,
    DB_USERNAME          = var.db_username,
    DB_PASSWORD          = var.db_password,
    DB_NAME              = var.db_name,
    WORKER_ID            = count.index,
    SPRING_PROFILES_ACTIVE = var.profiles_active,
    DOCKER_IMAGE         = var.docker_image
  })

  tags = {
    Name = "emsp_instance_${count.index}"
  }
}

# ALB
resource "aws_lb" "emsp_alb" {
  name               = "emsp-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = [aws_subnet.public.id, aws_subnet.publicB.id]
}

resource "aws_lb_target_group" "emsp_tg" {
  name     = "emsp-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    path                = "/health"
    port                = "8080"
    protocol            = "HTTP"
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 5
    interval            = 30
  }
}

resource "aws_lb_target_group_attachment" "emsp_tg_attachment" {
  count            = var.instance_count
  target_group_arn = aws_lb_target_group.emsp_tg.arn
  target_id        = aws_instance.emsp[count.index].id
  port             = 8080
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.emsp_alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.emsp_tg.arn
  }
}





