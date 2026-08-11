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
