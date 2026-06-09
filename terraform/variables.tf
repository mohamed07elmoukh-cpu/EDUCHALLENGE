variable "aws_region" {
  description = "AWS region for the infrastructure."
  type        = string
  default     = "eu-west-3"
}

variable "project_name" {
  description = "Project name used in resource naming."
  type        = string
  default     = "educhallenge"
}

variable "environment" {
  description = "Deployment environment name."
  type        = string
  default     = "dev"
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_a_cidr" {
  description = "CIDR block for the first public subnet."
  type        = string
  default     = "10.0.1.0/24"
}

variable "public_subnet_b_cidr" {
  description = "CIDR block for the second public subnet."
  type        = string
  default     = "10.0.2.0/24"
}

variable "instance_type" {
  description = "EC2 instance type for the application host."
  type        = string
  default     = "t3.micro"
}

variable "key_pair_name" {
  description = "Existing AWS key pair name used to connect to the EC2 instance."
  type        = string
}

variable "allowed_ssh_cidr" {
  description = "CIDR allowed to access EC2 over SSH."
  type        = string
  default     = "0.0.0.0/0"
}

variable "frontend_port" {
  description = "Frontend HTTP port exposed on the EC2 host."
  type        = number
  default     = 80
}

variable "backend_port" {
  description = "Backend HTTP port exposed on the EC2 host."
  type        = number
  default     = 8080
}

variable "create_ecr_repositories" {
  description = "Whether to create backend and frontend ECR repositories."
  type        = bool
  default     = true
}

variable "tags" {
  description = "Additional tags applied to AWS resources."
  type        = map(string)
  default     = {}
}
