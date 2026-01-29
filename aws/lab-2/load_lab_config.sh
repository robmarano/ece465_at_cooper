#!/usr/bin/env bash

source ./lab_config.sh

VPC_ID=$(aws ec2 describe-instances ${PREAMBLE} --filters Name=instance-state-name,Values=running Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE} --query "Reservations[0].Instances[0].VpcId" --output text)
echo "VPC_ID=${VPC_ID}"

SN_TAG_NAME=Subnet-Name
SN_TAG_VALUE=Public-Subnet
SN_ID_PUBLIC=$(aws ec2 describe-subnets ${PREAMBLE} --filter Name=vpc-id,Values=${VPC_ID} Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE} Name=tag:${SN_TAG_NAME},Values=${SN_TAG_VALUE} --query "Subnets[0].SubnetId" --output text)
echo "SN_ID_PUBLIC=${SN_ID_PUBLIC}"

SN_TAG_VALUE=Private-Subnet
SN_ID_PRIVATE=$(aws ec2 describe-subnets ${PREAMBLE} --filter Name=vpc-id,Values=${VPC_ID} Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE} Name=tag:${SN_TAG_NAME},Values=${SN_TAG_VALUE} --query "Subnets[0].SubnetId" --output text)
echo "SN_ID_PRIVATE=${SN_ID_PRIVATE}"

IGW_ID=$(aws ec2 describe-internet-gateways ${PREAMBLE} --filter Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE} --query "InternetGateways[0].InternetGatewayId" --output text)
echo "IGW_ID=${IGW_ID}"

RT_TABLE_ID=$(aws ec2 describe-route-tables ${PREAMBLE} --filter Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE},Name=route.gateway-id,Values=${IGW_ID} --query "RouteTables[*].RouteTableId" --output text)
echo "RT_TABLE_ID=${RT_TABLE_ID}"

RT_TABLE_ASSN_ID=$(aws ec2 describe-route-tables ${PREAMBLE} --filter Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE},Name=route.gateway-id,Values=${IGW_ID} --query "RouteTables[0].Associations[0].RouteTableAssociationId" --output text)
echo "RT_TABLE_ASSN_ID=${RT_TABLE_ASSN_ID}"

INSTANCES_IDS=$(aws ec2 describe-instances ${PREAMBLE} --filters Name=instance-state-name,Values=running Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE} --query "Reservations[*].Instances[*].InstanceId" --output text | tr '\n' ' ')
echo "Instances IDs: $INSTANCES_IDS"

SEC_GROUP_ID=$(aws ec2 describe-security-groups ${PREAMBLE} --filter Name=tag:${APP_TAG_NAME},Values=${APP_TAG_VALUE} --query "SecurityGroups[*].GroupId" --output text)
echo "SEC_GROUP_ID=${SEC_GROUP_ID}"
