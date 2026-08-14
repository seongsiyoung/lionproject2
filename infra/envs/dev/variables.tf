variable "aws_region" {
  type        = string
  description = "AWS region for dev infrastructure."
  default     = "ap-northeast-2"
}

variable "project" {
  type        = string
  description = "Project name used in resource names and tags."
  default     = "devsolve"
}

variable "environment" {
  type        = string
  description = "Environment name used in resource names and tags."
  default     = "dev"
}

variable "domain_name" {
  type        = string
  description = "Root domain name managed in Route 53."
  default     = "approach.shop"
}

variable "route53_zone_id" {
  type        = string
  description = "Route 53 hosted zone ID. When set, dev records are written to this exact zone instead of looking up by domain name."
  default     = ""
}

variable "api_domain" {
  type        = string
  description = "API domain name for the ALB certificate."
  default     = "api.approach.shop"
}

variable "files_domain" {
  type        = string
  description = "Files domain name for the later CloudFront distribution."
  default     = "files.approach.shop"
}

variable "lesson_file_cloudfront_public_key_encoded" {
  type        = string
  description = "PEM encoded CloudFront public key for lesson file signed URLs. The matching private key must be stored outside Terraform."
  sensitive   = false
}

variable "lesson_file_cors_allowed_origins" {
  type        = list(string)
  description = "Allowed browser origins for direct S3 lesson file uploads."
  default = [
    "https://approach.shop",
    "https://www.approach.shop",
    "http://localhost:5173"
  ]
}

variable "github_repository" {
  type        = string
  description = "GitHub repository allowed to assume deployment roles, formatted as owner/repo."
}

variable "github_oidc_subjects" {
  type        = list(string)
  description = "Allowed GitHub OIDC subject claims. Defaults to the repository's dev environment subject when empty."
  default     = []
}

variable "budget_email" {
  type        = string
  description = "Email address for AWS budget notifications in later resource stages."
}

variable "budget_limit_usd" {
  type        = string
  description = "Monthly budget limit in USD for the dev environment."
  default     = "10"
}

variable "db_name" {
  type        = string
  description = "Initial MySQL database name."
  default     = "devsolve"
}

variable "db_username" {
  type        = string
  description = "RDS master username used in the later database stage."
  default     = "devsolve_admin"
}
