terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.38.0"
    }
  }
}

provider "aws"{
  region = "us-east-1"
}


data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}


resource "aws_instance" "ec2_instance"{

  ami = data.aws_ami.amazon_linux.id
  instance_type = "t3.micro"
  subnet_id = aws_subnet.ec2_subnet.id
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]
  iam_instance_profile = aws_iam_instance_profile.ec2_profile.name
  associate_public_ip_address = true
  user_data = <<-EOF
    #!/bin/bash
    yum update -y
    yum install -y java-21-amazon-corretto
    mkdir -p /opt/app
    aws s3 cp s3://${var.s3_bucket_name}/${var.jar_file_name} /opt/app/app.jar
    
    cat > /etc/systemd/system/rag-app.service <<SERVICE
    [Unit]
    Description=RAG Spring Boot App
    After=network.target

    [Service]
    ExecStart=/usr/bin/java -jar /opt/app/app.jar --spring.profiles.active=prod
    WorkingDirectory=/opt/app
    Restart=always
    RestartSec=10
    StandardOutput=journal
    StandardError=journal

    [Install]
    WantedBy=multi-user.target
    SERVICE

    systemctl daemon-reload
    systemctl enable rag-app
    systemctl start rag-app

  EOF

  tags = {
    Name = "rag-ec2instanc"
  }

  
}

resource "aws_iam_role" "ec2_role" {
  name = "ec2-s3-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Action    = "sts:AssumeRole"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}




resource "aws_iam_role_policy" "s3_read" {
  role = aws_iam_role.ec2_role.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = ["s3:GetObject", "s3:ListBucket"]
        Resource = [
          "arn:aws:s3:::${var.s3_bucket_name}",
          "arn:aws:s3:::${var.s3_bucket_name}/*"
        ]
      },
      {
        Effect   = "Allow"
        Action   = [
          "secretsmanager:GetSecretValue",
          "secretsmanager:DescribeSecret",
          "secretsmanager:ListSecrets",
          "secretsmanager:CreateSecret"
        ]
        Resource = "*"
      }
    ]
  })
}


resource "aws_iam_instance_profile" "ec2_profile" {
  role = aws_iam_role.ec2_role.name
}


resource "aws_vpc" "ec2_vpc"{
  cidr_block       = "10.0.0.0/16"
  enable_dns_support = true
  enable_dns_hostnames = true
  tags = {
    Name = "rag-vpc"
  }
}

resource "aws_subnet" "ec2_subnet"{
  vpc_id = aws_vpc.ec2_vpc.id
  cidr_block = "10.0.1.0/24"
  availability_zone       = "us-east-1a" 
  map_public_ip_on_launch = true
  tags ={name = "rag-subnet"}
}

resource "aws_internet_gateway" "ec2_gateway" {
  vpc_id = aws_vpc.ec2_vpc.id

  tags = {
    Name = "rag-igw"
  }
}

resource "aws_route_table" "ec2_routetable" {
  vpc_id = aws_vpc.ec2_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.ec2_gateway.id
  }


  tags = {
    Name = "rag-routetable"
  }
}

resource "aws_route_table_association" "ec2_routeassociation" {
  subnet_id      = aws_subnet.ec2_subnet.id
  route_table_id = aws_route_table.ec2_routetable.id
}


resource "aws_security_group" "ec2_sg" {
  name        = "rag-sg"
  description = "Security Group to allow the communication"
  vpc_id      = aws_vpc.ec2_vpc.id

   ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]   # SSH
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]   # Spring Boot
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"          # -1 means ALL protocols
    cidr_blocks = ["0.0.0.0/0"] # allow all outbound
  }

  

  tags = {
    Name = "rag-sg"
  }
}





output "aws_ami_id"{
  value = data.aws_ami.amazon_linux.id
}