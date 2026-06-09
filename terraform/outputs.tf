output "ec2_public_ip" {
  description = "Public IP address of the EC2 instance."
  value       = aws_instance.app_host.public_ip
}

output "ec2_public_dns" {
  description = "Public DNS name of the EC2 instance."
  value       = aws_instance.app_host.public_dns
}

output "backend_ecr_repository_url" {
  description = "Backend ECR repository URL."
  value       = try(aws_ecr_repository.backend[0].repository_url, null)
}

output "frontend_ecr_repository_url" {
  description = "Frontend ECR repository URL."
  value       = try(aws_ecr_repository.frontend[0].repository_url, null)
}

output "ssh_hint" {
  description = "SSH command template for the EC2 instance."
  value       = "ssh -i <your-key>.pem ec2-user@${aws_instance.app_host.public_ip}"
}
