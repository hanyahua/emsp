variable "aws_region" {
  default = "ap-northeast-1a"
}

variable "instance_type" {
  default = "t2.micro"
}

variable "key_name" {
  description = "SSH Key pair name"
  type = string
}

variable "docker_image" {
  type = string
}

variable "db_username" {
  default = "root"
}

variable "db_password" {
  description = "RDS root password"
  type        = string
  sensitive   = true
}

variable "db_name" {
  default = "emsp"
}
