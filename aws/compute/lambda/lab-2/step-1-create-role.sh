#!/usr/bin/env bash

PROFILE=cooper
REGION=us-east-1
PREAMBLE="--profile ${PROFILE} --region ${REGION}"
AWS_CLI="aws ${PREAMBLE}"

#
# Step 1 - Create IAM Role
#

ROLE_NAME=RoleLambdaExecutionTEMP
ROLE_DESC="Allows Lambda functions to call AWS services on your behalf."
ROLE_MAX_DURATION=3600
read -r -d '' ROLE_TAGS <<-'EOF1'
	[
	    {
	        "Key": "type",
	        "Value": "lab"
	    },
	    {
	        "Key": "user",
	        "Value": "rob"
	    }
	]
EOF1

echo ${ROLE_TAGS}

read -r -d '' ROLE_POLICY <<-'EOF2'
	{
	    "Version": "2012-10-17",
	    "Statement": [
		    {
		        "Effect": "Allow",
		        "Principal": {
		            "Service": "lambda.amazonaws.com"
		        },
		        "Action": "sts:AssumeRole"
		    }
		]
	}
EOF2

echo \'${ROLE_POLICY}\'

###${AWS_CLI} iam create-role --role-name ${ROLE_NAME} --description \'${ROLE_DESC}\' --tags \'${ROLE_TAGS}\' --assume-role-policy-document \'${ROLE_POLICY}\'

cat <<-EOC > ${ROLE_NAME}.json
{
    "Path": "/",
    "RoleName": "${ROLE_NAME}",
    "Description": "${ROLE_DESCR}",
    "MaxSessionDuration": ${ROLE_MAX_DURATION},
    "PermissionsBoundary": "",
    "AssumeRolePolicyDocument": ${ROLE_POLICY},
    "Tags":
    	${ROLE_TAGS}
}
EOC
ROLE_CONFIG_JSON=$(cat ${ROLE_NAME}.json)
echo ${ROLE_CONFIG_JSON}

${AWS_CLI} iam create-role --cli-input-json ${ROLE_CONFIG_JSON}