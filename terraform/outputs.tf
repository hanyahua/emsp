output "app_public_ip" {
  value = aws_instance.emsp.public_ip
}

output "mysql_endpoint" {
  value = aws_db_instance.mysql.address
}