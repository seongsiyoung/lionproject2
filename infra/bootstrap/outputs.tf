output "state_bucket" {
  description = "S3 bucket name for Terraform remote state."
  value       = aws_s3_bucket.terraform_state.bucket
}

output "aws_region" {
  description = "AWS region for Terraform remote state."
  value       = var.aws_region
}

output "route53_zone_id" {
  description = "Route 53 hosted zone ID for the root domain."
  value       = aws_route53_zone.primary.zone_id
}

output "route53_name_servers" {
  description = "Name servers assigned to the Route 53 hosted zone. Configure these at the domain registrar before ACM validation."
  value       = aws_route53_zone.primary.name_servers
}
