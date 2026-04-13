resource "aws_s3_bucket" "frontend" {
  bucket = "${var.s3_angular}-frontend"

  tags = {
    Name        = "RagAngular"
  }
}


resource "aws_s3_bucket_public_access_block" "public_access" {
  bucket = aws_s3_bucket.frontend.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}


resource "aws_cloudfront_origin_access_control" "oac" {
  name                              = "${var.app_name}"
  description                       = "Origin Access Control"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}


resource "aws_cloudfront_distribution" "frontend" {
  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"
  comment             = "${var.app_name} Angular Frontend"

  # Where CloudFront pulls files from (your S3 bucket)
  origin {
    domain_name              = aws_s3_bucket.frontend.bucket_regional_domain_name
    origin_id                = "S3-${aws_s3_bucket.frontend.bucket}"
    origin_access_control_id = aws_cloudfront_origin_access_control.oac.id
  }


  # Default behavior: cache and serve Angular files
  default_cache_behavior {
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    target_origin_id       = "S3-${aws_s3_bucket.frontend.bucket}"
    viewer_protocol_policy = "redirect-to-https"  # Force HTTPS for the frontend

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = 3600   # Cache files for 1 hour
    max_ttl     = 86400  # Max cache 24 hours
  }

  # Angular routing fix: 403/404 from S3 → serve index.html
  # This is critical — without it, refreshing a page like /dashboard breaks
  custom_error_response {
    error_code            = 403
    response_code         = 200
    response_page_path    = "/index.html"
  }

  custom_error_response {
    error_code            = 404
    response_code         = 200
    response_page_path    = "/index.html"
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"  # Allow all countries
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true  # Free HTTPS via CloudFront domain
  }

  tags = {
    Name        = "${var.app_name}-cloudfront"
  }


  }

  # ─────────────────────────────────────────
# S3 Bucket Policy
# Allows ONLY CloudFront to read from S3
# ─────────────────────────────────────────
resource "aws_s3_bucket_policy" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowCloudFrontRead"
        Effect = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action   = "s3:GetObject"
        Resource = "${aws_s3_bucket.frontend.arn}/*"
        Condition = {
          StringEquals = {
            # Only allow THIS CloudFront distribution to access S3
            "AWS:SourceArn" = aws_cloudfront_distribution.frontend.arn
          }
        }
      }
    ]
  })
}



