# API Gateway Example
[API GW + Lambda Proxy](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html
)

[Setting up CloudWatch Logs](https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-logging.html)

Use Postman or cURL to test:

POST using URL https://puvcjeyzs2.execute-api.us-east-1.amazonaws.com/test/greeting?greeter=jane
add a HEADER
key: authorization
value: AWS4-HMAC-SHA256 Credential={access_key}/20171020/us-west-2/execute-api/aws4_request,SignedHeaders=content-type;host;x-amz-date, Signature=f327...5751'


