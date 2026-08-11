output "state_bucket" {
  description = "S3 bucket name for Terraform remote state."
  value       = aws_s3_bucket.terraform_state.bucket
}

output "aws_region" {
  description = "AWS region for Terraform remote state."
  value       = var.aws_region
}
