output "app_public_ip" {
  value = aws_lb.emsp_alb.dns_name
}

output "mysql_endpoint" {
  value = aws_db_instance.mysql.address
}