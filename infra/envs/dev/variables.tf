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
