#!/usr/bin/env bash

VPC_ID=$1
KEY_NAME=$2
# for example KEY_NAME="lab1"
SN_ID_PUBLIC=$3

# for Amazon Linux 2 on x86_64
AMI_ID=ami-047a51fa27710816e

PROFILE=default
REGION=us-east-1
PREAMBLE="--profile ${PROFILE} --region ${REGION}"

SEC_GROUP_ID=$(aws ec2 create-security-group ${PREAMBLE} --group-name SSHAccess --description "Security group for SSH access" --vpc-id ${VPC_ID}) | jq '.GroupId' | tr -d '"'

aws ec2 authorize-security-group-ingress ${PREAMBLE} --group-id ${GROUP_ID} --protocol tcp --port 22 --cidr 0.0.0.0/0
INSTANCE_ID=$(aws ec2 run-instances ${PREAMBLE} --image-id ${AMI_ID} --count 1 --instance-type t2.micro --key-name ${KEY_NAME} --security-group-ids ${SEC_GROUP_ID} --subnet-id ${SN_ID_PUBLIC}) | jq '.Instances[0].InstanceId' | tr -d '"'

aws ec2 describe-instances ${PREAMBLE} --instance-id ${INSTANCE_ID}
