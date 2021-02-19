#!/usr/bin/env bash

# load up variables
source ./lab_config.sh

NOW=$(date '+%Y%m%d%H%M%S')
LOGFILE="./create_keypair-${NOW}.log"
echo "Removing Full AWS infrastructure for " | tee ${LOGFILE}

echo "Running create_keypair.sh at ${NOW}" | tee -a ${LOGFILE}

aws ec2 create-key-pair ${PREAMBLE} --key-name ${KEY_NAME} --query 'KeyMaterial' --output text > ${KEY_FILE}
chmod 400 ${KEY_FILE}

echo "Done." | tee -a ${LOGFILE}
exit 0