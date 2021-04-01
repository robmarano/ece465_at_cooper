#!/usr/bin/env bash

PROFILE=educate
REGION=us-east-1
PREAMBLE="--profile ${PROFILE} --region ${REGION}"

VPC_CDR=10.0.0.0/16
PUBLIC_CDR=10.0.1.0/24
PRIVATE_CDR=10.0.2.0/24

INSTANCE_TYPE=t2.micro
INSTANCES_COUNT=4
# for Amazon Linux 2 on x86_64
AMI_ID=ami-047a51fa27710816e
USER=ec2-user
KEY_NAME=lab1
KEY_FILE=~/.ssh/pems/${KEY_NAME}.pem

APP_TYPE=type
APP_TYPE_NAME=distributed-app
APP_TAG_NAME=APP
APP_TAG_VALUE=multi-node
