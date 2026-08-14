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

output "ecr_repository_url" {
  description = "ECR repository URL for backend Docker images."
  value       = aws_ecr_repository.backend.repository_url
}

output "backend_instance_id" {
  description = "Private EC2 instance ID used by SSM Run Command."
  value       = aws_instance.backend.id
}

output "alb_dns_name" {
  description = "DNS name of the public API ALB."
  value       = aws_lb.backend.dns_name
}

output "api_domain_name" {
  description = "API domain alias record pointing to the ALB."
  value       = aws_route53_record.api.fqdn
}

output "rds_endpoint" {
  description = "RDS MySQL endpoint address."
  value       = aws_db_instance.mysql.address
}

output "rds_master_user_secret_arn" {
  description = "Secrets Manager ARN for the RDS managed master user password."
  value       = aws_db_instance.mysql.master_user_secret[0].secret_arn
}

output "redis_endpoint" {
  description = "ElastiCache Redis endpoint address."
  value       = aws_elasticache_replication_group.redis.primary_endpoint_address
}

output "lesson_file_bucket" {
  description = "Private S3 bucket name for lesson files."
  value       = aws_s3_bucket.lesson_files.bucket
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution ID for lesson file delivery."
  value       = aws_cloudfront_distribution.lesson_files.id
}

output "cloudfront_domain_name" {
  description = "CloudFront distribution domain name for lesson file delivery."
  value       = aws_cloudfront_distribution.lesson_files.domain_name
}

output "files_domain_name" {
  description = "Route 53 alias record for lesson file delivery."
  value       = aws_route53_record.files.fqdn
}

output "cloudfront_public_key_id" {
  description = "CloudFront public key ID used as the application key pair ID."
  value       = aws_cloudfront_public_key.lesson_files.id
}

output "cloudfront_key_group_id" {
  description = "CloudFront key group ID trusted by the lesson file distribution."
  value       = aws_cloudfront_key_group.lesson_files.id
}

output "cloudwatch_log_groups" {
  description = "CloudWatch log groups created for application, Nginx, and deployment logs."
  value = [
    aws_cloudwatch_log_group.app.name,
    aws_cloudwatch_log_group.nginx_access.name,
    aws_cloudwatch_log_group.nginx_error.name,
    aws_cloudwatch_log_group.deploy.name
  ]
}

output "runtime_parameter_names" {
  description = "SSM parameter names created for EC2 runtime configuration."
  value = {
    aws_region           = aws_ssm_parameter.aws_region.name
    db_url               = aws_ssm_parameter.db_url.name
    db_username          = aws_ssm_parameter.db_username.name
    db_master_secret_arn = aws_ssm_parameter.db_master_secret_arn.name
    redis_host           = aws_ssm_parameter.redis_host.name
    redis_port           = aws_ssm_parameter.redis_port.name
    redis_password       = aws_ssm_parameter.redis_password.name
    lesson_file_bucket   = aws_ssm_parameter.lesson_file_s3_bucket.name
    cloudfront_domain    = aws_ssm_parameter.lesson_file_cloudfront_domain.name
    cloudfront_key_id    = aws_ssm_parameter.lesson_file_cloudfront_key_pair_id.name
  }
}
