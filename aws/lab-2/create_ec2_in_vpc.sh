#!/usr/bin/env bash

# load up variables
source ./lab_config.sh

NOW=$(date '+%Y%m%d%H%M%S')
LOGFILE="./create_ec2_in_vpc-${NOW}.log"
echo "Creating Full AWS infrastructure for ${APP_TAG_NAME}:${APP_TAG_VALUE}" | tee ${LOGFILE}

echo "Running create_ec2_in_vpc.sh at ${NOW}" | tee -a ${LOGFILE}

# create the security group for SSH on port 22
echo "Create the security group for SSH on port 22" | tee -a ${LOGFILE}
SEC_GROUP_ID=$(aws ec2 create-security-group ${PREAMBLE} --group-name SSHAccess --description "Security group for SSH access" --vpc-id ${VPC_ID} --tag-specifications "ResourceType=string,Tags=[{Key=${APP_TYPE},Value=${APP_TYPE_NAME},{Key=${APP_TAG_NAME},Value=${APP_TAG_VALUE}]" | jq '.GroupId' | tr -d '"')

# set the security group for ingress from the Internet
echo "Set the security group for ingress from the Internet" | tee -a ${LOGFILE}
aws ec2 authorize-security-group-ingress ${PREAMBLE} --group-id ${SEC_GROUP_ID} --protocol tcp --port 22 --cidr 0.0.0.0/0 | tee ${LOGFILE}

# create the instance(s) with tags
#INSTANCE_ID=$(aws ec2 run-instances ${PREAMBLE} --image-id ${AMI_ID} --count ${INSTANCES} --instance-type t2.micro --key-name ${KEY_NAME} --security-group-ids ${SEC_GROUP_ID} --subnet-id ${SN_ID_PUBLIC} | jq '.Instances[0].InstanceId' | tr -d '"')
echo "Create ${INSTANCES_COUNT} instances" | tee -a ${LOGFILE}
aws ec2 run-instances ${PREAMBLE} --image-id ${AMI_ID} --count ${INSTANCES_COUNT} --instance-type ${INSTANCE_TYPE} --key-name ${KEY_NAME} --security-group-ids ${SEC_GROUP_ID} --subnet-id ${SN_ID_PUBLIC} \
   --tag-specifications "ResourceType=instance,Tags=[{Key=${APP_TYPE},Value=${APP_TYPE_NAME},{Key=${APP_TAG_NAME},Value=${APP_TAG_VALUE}]" \
   "ResourceType=volume,Tags=[{Key=${APP_TYPE},Value=${APP_TYPE_NAME},{Key=${APP_TAG_NAME},Value=${APP_TAG_VALUE}]" | tee ${LOGFILE}

#aws ec2 describe-instances ${PREAMBLE} --instance-ids ${INSTANCE_ID}

#aws ec2 describe-instances --filters "Name=instance-type,Values=t2.micro" --query "Reservations[].Instances[].InstanceId"  --output json | jq '.[]' | tr -d '"'

# get instances IDs for those running and are part of tags
echo "Fetch instances IDs" | tee -a ${LOGFILE}
INSTANCES_IDS=$(aws ec2 describe-instances ${PREAMBLE} --filters Name=instance-state-name,Values=running Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE} --query "Reservations[*].Instances[*].InstanceId" --output text | tr '\n' ' ')
echo "Instances IDs: $INSTANCES_IDS" | tee ${LOGFILE}

# get public IP addresses of the instances (in the public subnet)
echo "Fetch public IP addresses of the instances" | tee -a ${LOGFILE}
INSTANCES_IPS=$(aws ec2 describe-instances ${PREAMBLE} --filters Name=instance-state-name,Values=running Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE} --query 'Reservations[*].Instances[*].[PublicIpAddress]' --output text | tr '\n' ' ')

echo "Public IP addresses: ${INSTANCES_IPS}" | tee ${LOGFILE}

exit 0
