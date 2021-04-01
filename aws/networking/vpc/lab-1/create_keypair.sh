#!/usr/bin/env bash

KEY_NAME="lab1"
FILE_NAME=~/.ssh/${KEY_NAME}.pem

PROFILE=default
REGION=us-east-1
PREAMBLE="--profile ${PROFILE} --region ${REGION}"

aws ec2 create-key-pair ${PREAMBLE} --key-name ${KEY_NAME} --query 'KeyMaterial' --output text > ${FILE_NAME}
chmod 400 ${FILE_NAME}