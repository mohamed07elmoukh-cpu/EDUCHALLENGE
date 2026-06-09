# Simple AWS Terraform for EduChallenge

This is the only Terraform stack kept in the project.

It is intentionally simple and creates:

- one VPC
- two public subnets
- one Internet Gateway
- one public route table
- one EC2 instance to host the application
- one security group
- one IAM role + instance profile for EC2
- two ECR repositories:
  - `educhallenge-backend`
  - `educhallenge-frontend`

## Why this version is simple

It does **not** create EKS, ECS, ALB, or RDS.

The idea is:

- push Docker images to ECR
- connect to the EC2 instance
- run your containers there with Docker or Docker Compose

That makes it much easier to explain in a presentation.

## Usage

```powershell
cd terraform
copy terraform.tfvars.example terraform.tfvars
terraform init
terraform plan
terraform apply
```

## Important variable

You must set:

```hcl
key_pair_name = "your-existing-aws-keypair-name"
```

This key pair must already exist in your AWS account and in the same AWS region.

## Outputs

After `terraform apply`, Terraform returns:

- EC2 public IP
- EC2 public DNS
- backend ECR repository URL
- frontend ECR repository URL

## Suggested oral explanation

"This Terraform stack is intentionally simple. It provisions the base AWS network, one EC2 host for the application, and ECR repositories for our frontend and backend images."
