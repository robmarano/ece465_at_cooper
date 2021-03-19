#!/usr/bin/env bash

NOW=$(date "+%Y%m%d%H%M%S")

# Setup AWS Environment
REGION=us-east-1
PROFILE=default
PREAMBLE="--region ${REGION} --profile ${PROFILE}"
AWS_CLI="aws ${PREAMBLE}"

BUCKET="cooper-union-ece465-spring-2021-rob"

# List buckets
${AWS_CLI} s3 ls

PREFIX="data"
FILE="file-${NOW}.json"

cat << EOF > ./${FILE}
{
	"course_name":"Cloud Computing",
	"course_number":"ECE 465",
	"timestamp":"${NOW}"
}
EOF

S3Uri="s3://${BUCKET}/${PREFIX}/${FILE}"

# Copy local file just generated to S3
${AWS_CLI} s3 cp ./${FILE} ${S3Uri}

# List what's in the bucket
${AWS_CLI} s3 ls ${BUCKET}

# Copy network object to local
S3Uri="s3://${BUCKET}/${PREFIX}/file.json"
${AWS_CLI} s3 cp ${S3Uri} ./downloaded-${NOW}.json

# Delete the file in S3
S3Uri="s3://${BUCKET}/${PREFIX}/file.json"
${AWS_CLI} s3 rm ${S3Uri}
