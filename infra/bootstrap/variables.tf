variable "aws_region" {
  type        = string
  description = "AWS region for Terraform bootstrap resources."
  default     = "ap-northeast-2"
}

variable "project" {
  type        = string
  description = "Project name used in bootstrap resource names and tags."
  default     = "devsolve"
}

variable "environment" {
  type        = string
  description = "Environment name used in bootstrap resource names and tags."
  default     = "dev"
}

variable "state_bucket_name" {
  type        = string
  description = "Optional globally unique S3 bucket name for Terraform remote state."
  default     = null
}

variable "domain_name" {
  type        = string
  description = "Root domain name for the shared Route 53 hosted zone."
  default     = "approach.shop"
}

variable "github_repository" {
  type        = string
  description = "GitHub repository allowed to assume the bootstrap-created infrastructure role, formatted as owner/repo."
}

variable "github_oidc_subjects" {
  type        = list(string)
  description = "Allowed GitHub OIDC subject claims. Defaults to the repository's environment subject when empty."
  default     = []
}

variable "github_oidc_provider_arn" {
  type        = string
  description = "Existing GitHub OIDC provider ARN. Leave empty to create a new provider in this bootstrap stack."
  default     = ""
}
