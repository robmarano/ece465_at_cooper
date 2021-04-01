#!/usr/bin/env bash

source ./lab_config.sh

ECR_REPO_NAME="hello-repository"

ACCOUNT_ID=$(${AWS_CLI} sts get-caller-identity | jq '.Account' | tr -d '"')
echo ${ACCOUNT_ID}