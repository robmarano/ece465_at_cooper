#!/usr/bin/env bash

# fetch config from AWS for currently running infrastructure
source ./load_lab_config.sh


NOW=$(date '+%Y%m%d%H%M%S')

${AWS_CLI} ecr delete-repository --repository-name ${ECR_REPO_NAME} --force

exit 0
