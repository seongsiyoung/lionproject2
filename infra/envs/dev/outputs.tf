output "vpc_id" {
  description = "VPC ID for dev infrastructure."
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "Public subnet IDs for ALB and NAT Gateway."
  value       = [aws_subnet.public_a.id, aws_subnet.public_b.id]
}

output "private_app_subnet_ids" {
  description = "Private app subnet IDs for EC2 and future ASG expansion."
  value       = [aws_subnet.private_app_a.id, aws_subnet.private_app_b.id]
}

output "private_data_subnet_ids" {
  description = "Private data subnet IDs for RDS and ElastiCache subnet groups."
  value       = [aws_subnet.private_data_a.id, aws_subnet.private_data_b.id]
}

output "alb_security_group_id" {
  description = "Security group ID for the later ALB."
  value       = aws_security_group.alb.id
}

output "ec2_security_group_id" {
  description = "Security group ID for the later private EC2 instance."
  value       = aws_security_group.ec2.id
}

output "rds_security_group_id" {
  description = "Security group ID for the later RDS instance."
  value       = aws_security_group.rds.id
}

output "redis_security_group_id" {
  description = "Security group ID for the later ElastiCache Redis node."
  value       = aws_security_group.redis.id
}

output "github_infra_role_arn" {
  description = "IAM role ARN for GitHub Actions infrastructure workflow."
  value       = aws_iam_role.github_infra.arn
}

output "github_deploy_role_arn" {
  description = "IAM role ARN for GitHub Actions app deployment workflow."
  value       = aws_iam_role.github_deploy.arn
}

output "api_certificate_arn" {
  description = "ACM certificate ARN for the API ALB."
  value       = aws_acm_certificate.api.arn
}

output "route53_zone_id" {
  description = "Route 53 hosted zone ID for the root domain."
  value       = data.aws_route53_zone.primary.zone_id
}

output "route53_name_servers" {
  description = "Name servers assigned to the Route 53 hosted zone."
  value       = data.aws_route53_zone.primary.name_servers
}
