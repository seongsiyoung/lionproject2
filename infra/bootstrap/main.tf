terraform {
  required_version = ">= 1.10.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = var.project
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

locals {
  name_prefix       = "${var.project}-${var.environment}"
  state_bucket_name = coalesce(var.state_bucket_name, "${local.name_prefix}-${data.aws_caller_identity.current.account_id}-terraform-state")

  github_sub_conditions = length(var.github_oidc_subjects) > 0 ? var.github_oidc_subjects : ["repo:${var.github_repository}:environment:${var.environment}"]
}

data "aws_caller_identity" "current" {}

resource "aws_s3_bucket" "terraform_state" {
  bucket = local.state_bucket_name
}

resource "aws_s3_bucket_versioning" "terraform_state" {
  bucket = aws_s3_bucket.terraform_state.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "terraform_state" {
  bucket = aws_s3_bucket.terraform_state.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "terraform_state" {
  bucket = aws_s3_bucket.terraform_state.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_route53_zone" "primary" {
  name = var.domain_name

  tags = {
    Name = "${local.name_prefix}-zone"
  }
}

resource "aws_iam_openid_connect_provider" "github" {
  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1"]
}

data "aws_iam_policy_document" "github_infra_assume_role" {
  statement {
    actions = ["sts:AssumeRoleWithWebIdentity"]
    effect  = "Allow"

    principals {
      type        = "Federated"
      identifiers = [aws_iam_openid_connect_provider.github.arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values   = local.github_sub_conditions
    }
  }
}

resource "aws_iam_role" "github_infra" {
  name               = "${local.name_prefix}-github-infra-role"
  assume_role_policy = data.aws_iam_policy_document.github_infra_assume_role.json
}

resource "aws_iam_role_policy" "github_infra" {
  name = "${local.name_prefix}-github-infra-policy"
  role = aws_iam_role.github_infra.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "acm:*",
          "application-autoscaling:*",
          "autoscaling:*",
          "budgets:*",
          "cloudfront:*",
          "cloudwatch:*",
          "ec2:*",
          "ecr:*",
          "elasticache:*",
          "elasticloadbalancing:*",
          "iam:*",
          "logs:*",
          "rds:*",
          "route53:*",
          "s3:*",
          "secretsmanager:*",
          "ssm:*"
        ]
        Resource = "*"
      }
    ]
  })
}
