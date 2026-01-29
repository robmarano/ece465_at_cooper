#!/usr/bin/env bash

API_ID=f28b8wlw7a
REGION=us-east-1
API_STAGE=test
API_METHOD=helloworld
API_PARAMS="name=John&city=Seattle"
API_LINK="https://${API_ID}.execute-api.${REGION}.amazonaws.com/${API_STAGE}/${API_METHOD}?${API_PARAMS}"

CURL_CMD=POST
curl -v -X ${CURL_CMD} \
  ${API_LINK} \
  -H 'content-type: application/json' \
  -H 'day: Thursday' \
  -d '{ "time": "evening" }'

CURL_CMD=GET
curl -X ${CURL_CMD} \
  ${API_LINK} \
  -H 'content-type: application/json' \
  -H 'day: Thursday'