terraform {
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
}

resource "aws_subnet" "public" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
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

resource "aws_security_group" "emsp_sg" {
  name        = "emsp_sg"
  description = "Allow HTTP SSH"
  vpc_id = aws_vpc.main.id

  ingress {
      description = "HTTP"
      from_port   = 80
      to_port     = 80
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
  }
  ingress  {
      description = "SSH"
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
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
  subnet_ids = [aws_subnet.public.id]
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
  instance_class = "db.t2.micro"
  identifier = "mysql"
}

resource "aws_instance" "emsp" {
  ami = "ami-054400ced365b82a0" #Ubuntu Server 24.04 LTS (HVM)
  instance_type               = var.instance_type
  subnet_id                   = aws_subnet.public.id
  key_name                    = var.key_name
  security_groups             = [aws_security_group.emsp_sg.name]
  associate_public_ip_address = true

  user_data                   = <<-EOF
      #!/bin/bash
      sudo apt update -y
      apt install docker.io -y
      systemctl start docker
      docker run -d -p 80:8080 \
                -e DB_HOST=${aws_db_instance.mysql.address} \
                -e DB_USERNAME=${var.db_username} \
                -e DB_PASSWORD=${var.db_password} \
                -e DB_NAME=${var.db_name} \
                ${var.docker_image}
      EOF
  tags = {
    Name = "emsp_instance"
  }
}




