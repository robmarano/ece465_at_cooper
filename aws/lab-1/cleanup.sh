#!/usr/bin/env bash

VPC_ID=$1
INSTANCE_ID=$2
SEC_GROUP_ID=$3
SN_ID_PUBLIC=$4
SN_ID_PRIVATE=$5
IGW_ID=$6
RT_TABLE_ID=$7

PROFILE=default
REGION=us-east-1
PREAMBLE="--profile ${PROFILE} --region ${REGION}"

# terminate EC2 instance
aws ec2 terminate-instances ${PREAMBLE} --instance-ids ${INSTANCE_ID}
aws ec2 describe-instances ${PREAMBLE} --instance-id ${INSTANCE_ID}

#delete sec group
aws ec2 delete-security-group ${PREAMBLE} --group-id ${SEC_GROUP_ID}

# delete subnets
aws ec2 delete-subnet ${PREAMBLE} --subnet-id ${SN_ID_PUBLIC}
aws ec2 delete-subnet ${PREAMBLE} --subnet-id ${SN_ID_PRIVATE}

# delete custom route tables
aws ec2 delete-route-table ${PREAMBLE} --route-table-id ${RT_TABLE_ID}

# detach IGW from your VPC
aws ec2 detach-internet-gateway ${PREAMBLE} --internet-gateway-id ${IGW_ID} --vpc-id ${VPC_ID}

# delete IGW aws ec2 delete-internet-gateway --internet-gateway-id igw-1ff7a07b

# delete your VPC
aws ec2 delete-vpc ${PREAMBLE} --vpc-id ${VPC_ID}


