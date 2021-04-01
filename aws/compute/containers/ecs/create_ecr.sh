#!/usr/bin/env bash

source ./load_lab_config.sh

# Create an Amazon ECR repository to store your hello-world image.
# Note the repositoryUri in the output.
REPO_URI=$(${AWS_CLI} ecr create-repository --repository-name ${ECR_REPO_NAME} | jq '.repository.repositoryUri' | tr -d '"')
echo ${REPO_URI}

# Tag the hello-world image with the repositoryUri value from the previous step.
TAG_OUTPUT=$(docker tag hello-world ${REPO_URI})
echo ${TAG_OUTPUT}

# Specify the registry URI you want to authenticate to
${AWS_CLI} ecr get-login-password | docker login --username AWS --password-stdin ${REPO_URI}

# Push the image to Amazon ECR with the repositoryUri value from the earlier step
docker push ${REPO_URI}

