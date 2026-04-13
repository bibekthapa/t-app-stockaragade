# deploy-frontend.sh

# 1. Build Angular app
cd /trade-app/   # adjust path
ng build --configuration production

# 2. Get bucket name from Terraform output
cd terraform
S3_BUCKET=$(terraform output -raw s3_bucket_name)
CLOUDFRONT_ID=$(terraform output -raw cloudfront_distribution_id)

# 3. Upload built files to S3
cd ..
aws s3 sync dist/trade-app/browser/ s3://$S3_BUCKET/ --delete
#                                        ↑
#                          check your dist folder name after ng build

# 4. Invalidate CloudFront cache
aws cloudfront create-invalidation \
  --distribution-id $CLOUDFRONT_ID \
  --paths "/*"

echo "Done"


