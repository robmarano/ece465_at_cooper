# https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html

aws --profile educate --region us-east-1  iam create-role --role-name lambda-ex --assume-role-policy-document file://lambda-trust-policy.json
{
    "Role": {
        "Path": "/",
        "RoleName": "lambda-ex",
        "RoleId": "AROA56WBWNNBLSQ5BPONK",
        "Arn": "arn:aws:iam::959257668418:role/lambda-ex",
        "CreateDate": "2021-04-15T22:53:12Z",
        "AssumeRolePolicyDocument": {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "",
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "apigateway.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        }
    }
}

aws --profile educate --region us-east-1 iam create-role --role-name apigateway-ex --assume-role-policy-document file://api-gw-trust-policy.json
{
    "Role": {
        "Path": "/",
        "RoleName": "apigateway-ex",
        "RoleId": "AROA56WBWNNBIVDIW7RFP",
        "Arn": "arn:aws:iam::959257668418:role/apigateway-ex",
        "CreateDate": "2021-04-15T22:56:33Z",
        "AssumeRolePolicyDocument": {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "",
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "apigateway.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        }
    }
}

aws --profile educate --region us-east-1  apigateway create-rest-api --name 'HelloWorld'
{
    "id": "puvcjeyzs2",
    "name": "HelloWorld",
    "createdDate": 1618531906,
    "apiKeySource": "HEADER",
    "endpointConfiguration": {
        "types": [
            "EDGE"
        ]
    },
    "disableExecuteApiEndpoint": false
}

aws --profile educate --region us-east-1  apigateway get-resources --rest-api-id puvcjeyzs2
{
    "items": [
        {
            "id": "bd7pak7c3m",
            "path": "/"
        }
    ]
}

aws --profile educate --region us-east-1  apigateway create-resource --rest-api-id puvcjeyzs2 \
      --region us-east-1 \
      --parent-id bd7pak7c3m \
      --path-part {proxy+}
{
    "id": "9l3ou3",
    "parentId": "bd7pak7c3m",
    "pathPart": "{proxy+}",
    "path": "/{proxy+}"
}

aws --profile educate --region us-east-1  apigateway put-method --rest-api-id puvcjeyzs2 \
--resource-id 9l3ou3 \
--http-method ANY \
--authorization-type "NONE"
{
    "httpMethod": "ANY",
    "authorizationType": "NONE",
    "apiKeyRequired": false
}

aws --profile educate --region us-east-1  apigateway put-integration \
  --rest-api-id puvcjeyzs2 \
  --resource-id 9l3ou3 \
  --http-method ANY \
  --type AWS_PROXY \
  --integration-http-method POST \
  --uri arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/arn:aws:lambda:us-east-1:959257668418:function:my-function/invocations \
  --credentials arn:aws:iam::959257668418:role/apigateway-ex
{
    "type": "AWS_PROXY",
    "httpMethod": "POST",
    "uri": "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/arn:aws:lambda:us-east-1:959257668418:function:my-function/invocations",
    "credentials": "arn:aws:iam::959257668418:role/apigateway-ex",
    "passthroughBehavior": "WHEN_NO_MATCH",
    "timeoutInMillis": 29000,
    "cacheNamespace": "9l3ou3",
    "cacheKeyParameters": []
}

aws --profile educate --region us-east-1  apigateway get-resources --rest-api-id puvcjeyzs2
{
    "items": [
        {
            "id": "9l3ou3",
            "parentId": "bd7pak7c3m",
            "pathPart": "{proxy+}",
            "path": "/{proxy+}",
            "resourceMethods": {
                "ANY": {}
            }
        },
        {
            "id": "bd7pak7c3m",
            "path": "/"
        }
    ]
}

aws --profile educate --region us-east-1  apigateway create-deployment --rest-api-id puvcjeyzs2 --stage-name test
{
    "id": "vp7zu8",
    "createdDate": 1618532083
}

