#!/usr/bin/env bash

# load up variables
source ./lab_config.sh

NOW=$(date '+%Y%m%d%H%M%S')
LOGFILE="./create_vpc-${NOW}.log"
echo "Creating Full AWS infrastructure for ${APP_TAG_NAME}:${APP_TAG_VALUE}" | tee ${LOGFILE}

echo "Running create_vpc.sh at ${NOW}" | tee -a ${LOGFILE}

# Step 1 - create VPC
VPC_ID=$(aws ec2 create-vpc ${PREAMBLE} --cidr-block ${VPC_CDR} | jq '.Vpc.VpcId' | tr -d '"')
echo "VPC_ID=${VPC_ID}" | tee -a ${LOGFILE}
read -rsp $'Press any key to continue...\n' -n1 key

# Step 2 - create public subnet
SN_TAG_NAME=Subnet-Name
SN_TAG_VALUE=Public-Subnet
SN_ID_PUBLIC=$(aws ec2 create-subnet ${PREAMBLE} --vpc-id ${VPC_ID} --cidr-block ${PUBLIC_CDR} --tag-specifications "ResourceType=string,Tags=[{Key=${APP_TYPE},Value=${APP_TYPE_NAME},{Key=${APP_TAG_NAME},Value=${APP_TAG_VALUE},{Key=${SN_TAG_NAME},Value=${SN_TAG_VALUE}]" | jq '.Subnet.SubnetId' | tr -d '"')
echo "SN_ID_PUBLIC = ${SN_ID_PUBLIC}" | tee -a ${LOGFILE}
read -rsp $'Press any key to continue...\n' -n1 key

# Step 3 - create private subnet
SN_TAG_NAME=Subnet-Name
SN_TAG_VALUE=Private-Subnet
SN_ID_PRIVATE=$(aws ec2 create-subnet ${PREAMBLE} --vpc-id ${VPC_ID} --cidr-block ${PRIVATE_CDR} --tag-specifications "ResourceType=string,Tags=[{Key=${APP_TYPE},Value=${APP_TYPE_NAME},{Key=${APP_TAG_NAME},Value=${APP_TAG_VALUE},{Key=${SN_TAG_NAME},Value=${SN_TAG_VALUE}]" | jq '.Subnet.SubnetId' | tr -d '"')
echo "SN_ID_PRIVATE=${SN_ID_PRIVATE}" | tee -a ${LOGFILE}
read -rsp $'Press any key to continue...\n' -n1 key

# Step 4 - create Internet Gateway (igw)
IGW_ID=$(aws ec2 create-internet-gateway ${PREAMBLE} --tag-specifications "ResourceType=string,Tags=[{Key=${APP_TYPE},Value=${APP_TYPE_NAME},{Key=${APP_TAG_NAME},Value=${APP_TAG_VALUE}]" | jq '.InternetGateway.InternetGatewayId' | tr -d '"')
echo "IGW_ID=${IGW_ID}" | tee -a ${LOGFILE}
read -rsp $'Press any key to continue...\n' -n1 key

# Step 5 - attach igw to VPC
aws ec2 attach-internet-gateway ${PREAMBLE} --vpc-id ${VPC_ID} --internet-gateway-id ${IGW_ID} | tee -a ${LOGFILE}
# check if not true then exit warning user...
read -rsp $'Press any key to continue...\n' -n1 key

# Step 6 - create a custom route table for VPC
RT_TABLE_ID=$(aws ec2 create-route-table ${PREAMBLE} --vpc-id ${VPC_ID} --tag-specifications "ResourceType=string,Tags=[{Key=${APP_TYPE},Value=${APP_TYPE_NAME},{Key=${APP_TAG_NAME},Value=${APP_TAG_VALUE}]" | jq '.RouteTable.RouteTableId' | tr -d '"')
echo "RT_TABLE_ID=${RT_TABLE_ID}" | tee -a ${LOGFILE}
read -rsp $'Press any key to continue...\n' -n1 key

# Step 7 - create a route to the Internet from the public subnet
aws ec2 create-route ${PREAMBLE} --route-table-id ${RT_TABLE_ID} --destination-cidr-block 0.0.0.0/0 --gateway-id ${IGW_ID} | tee -a ${LOGFILE}
# check if not true then exit warning user...

# Step 8 - describe your routes
aws ec2 describe-route-tables ${PREAMBLE} --route-table-id ${RT_TABLE_ID} | tee -a ${LOGFILE}
#cat route-tables.json | jq '.RouteTables[0].Routes'

# Step 9 - describe your routes on the VPC and subnet
aws ec2 describe-subnets ${PREAMBLE} --filters "Name=vpc-id,Values=${VPC_ID}" --query 'Subnets[*].{ID:SubnetId,CIDR:CidrBlock}' | tee -a ${LOGFILE}
read -rsp $'Press any key to continue...\n' -n1 key

# Step 10 - associate table to public subnet
ASSN_ID=$(aws ec2 associate-route-table ${PREAMBLE} --subnet-id ${SN_ID_PUBLIC} --route-table-id ${RT_TABLE_ID} | jq '.AssociationId' | tr -d ',' | tr -d '"')
echo "ASSN_ID=${ASSN_ID}" | tee -a ${LOGFILE}
read -rsp $'Press any key to continue...\n' -n1 key

# Step 11 - provide a public IP address for any node in the subnet
aws ec2 modify-subnet-attribute ${PREAMBLE} --subnet-id ${SN_ID_PUBLIC} --map-public-ip-on-launch | tee -a ${LOGFILE}
echo "Done." | tee -a ${LOGFILE}

exit 0


