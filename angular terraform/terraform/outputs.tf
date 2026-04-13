# Add these to your existing outputs.tf

output "cloudfront_url" {
  description = "Your Angular app URL (use this in the browser)"
  value       = "https://${aws_cloudfront_distribution.frontend.domain_name}"
}

output "s3_bucket_name" {
  description = "S3 bucket name (needed for deploy script)"
  value       = aws_s3_bucket.frontend.bucket
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution ID (needed to invalidate cache after deploy)"
  value       = aws_cloudfront_distribution.frontend.id
}


output "ec2_public_ip" {
  description = "EC2 backend IP"
  value       = aws_instance.ec2_instance.public_ip  # adjust to match your EC2 resource name
}