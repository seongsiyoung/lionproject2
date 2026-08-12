terraform {
  backend "s3" {
    bucket       = "devsolve-dev-448830788551-terraform-state"
    key          = "envs/dev/terraform.tfstate"
    region       = "ap-northeast-2"
    encrypt      = true
    use_lockfile = true
  }
}
