#!/usr/bin/env bash

VPC_CDR=10.0.0.0/16
PUBLIC_CDR=10.0.1.0/24
PRIVATE_CDR=10.0.2.0/24

PROFILE=default
REGION=us-east-1
PREAMBLE="--profile ${PROFILE} --region ${REGION}"

# Step 1 - create VPC
VPC_ID=$(aws ec2 create-vpc ${PREAMBLE} --cidr-block ${VPC_CDR}) | jq '.Vpc.VpcId' | tr -d '"'

# Step 2 - create public subnet
SN_ID_PUBLIC=$(aws ec2 create-subnet ${PREAMBLE} --vpc-id ${VPC_ID} --cidr-block ${PUBLIC_CDR}) | jq '.Subnet.SubnetId' | tr -d '"'

# Step 3 - create private subnet
SN_ID_PRIVATE=$(aws ec2 create-subnet ${PREAMBLE} --vpc-id ${VPC_ID} --cidr-block ${PRIVATE_CDR}) | jq '.Subnet.SubnetId' | tr -d '"'

# Step 4 - create Internet Gateway (igw)
IGW_ID=$(aws ec2 create-internet-gateway ${PREAMBLE}) | jq '.InternetGateway.InternetGatewayId' | tr -d '"'

# Step 5 - attach igw to VPC
RETURN_ATTACH_IGW=$(aws ec2 attach-internet-gateway ${PREAMBLE} --vpc-id ${VPC_ID} --internet-gateway-id ${IGW_ID}) | jq '.Return'
# check if not true then exit warning user...

# Step 6 - create a custom route table for VPC
RT_TABLE_ID=$(aws ec2 create-route-table ${PREAMBLE} --vpc-id ${VPC_ID}) | jq '.RouteTable.RouteTableId' | tr -d '"'

# Step 7 - create a route to the Internet from the subnet
aws ec2 create-route ${PREAMBLE} --route-table-id ${RT_TABLE_ID} --destination-cidr-block 0.0.0.0/0 --gateway-id ${IGW_ID}

# Step 8 - describe your routes
aws ec2 describe-route-tables ${PREAMBLE} --route-table-id ${RT_TABLE_ID}
#cat route-tables.json | jq '.RouteTables[0].Routes'

# Step 9 - 
aws ec2 describe-subnets ${PREAMBLE} --filters "Name=vpc-id,Values=${VPC_ID}" --query 'Subnets[*].{ID:SubnetId,CIDR:CidrBlock}'

# Step 10 - associate table to public subnet
ASSN_ID=$(aws ec2 associate-route-table ${PREAMBLE} --subnet-id ${SN_ID_PUBLIC} --route-table-id ${RT_TABLE_ID}) | jq '.AssociationId' | tr -d ',' | tr -d '"'

# Step 11 - provide a public IP address for any node in the subnet
aws ec2 modify-subnet-attribute ${PREAMBLE} --subnet-id ${SN_ID_PUBLIC} --map-public-ip-on-launch

