# Lambda Power Tools for Java - Idempotency Module - Amazon API Gateway
This project contains source code and supporting files for a serverless application that you can deploy with SAM CLI. 
The application uses AWS Power Tools for Java to implement a lambda function that processes requests from API Gateway in an idempotent way using AWS Lambda Power Tools Idempotency module.
For more information on the module, refer to the [documentation](https://awslabs.github.io/aws-lambda-powertools-java/utilities/idempotency/)

The project contains following files and directories:

- src/main/java - Code for the Lambda function.
- src/main/api - API contract in OpenAPI Specification ([Swagger](https://swagger.io/specification/)).
- src/test/java - Unit tests.
- template.yaml - A template that defines the application's AWS resources.

The application uses several AWS resources, including Amazon DynamoDB table, Lambda function and API Gateway REST API. These resources are defined in the `template.yaml` file. You can update the template to add AWS resources through the same deployment process that updates your application code.

The application uses Dagger as a Dependency Injection Framework. For more information, refer to the [documentation](https://dagger.dev/)

## Architecture
The application exposes a REST API with a single POST /orders endpoint for submitting an order request.
The request is processed synchronously by an idempotent lambda function which returns an orderId.

![Architecture diagram](../docs/media/idempotency-apigw.png)

## Deploy the sample application

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. It uses Docker to run your functions in an Amazon Linux environment that matches Lambda. It can also emulate your application's build environment and API.

To use the SAM CLI, you need the following tools.

* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* Java11 - [Install the Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* Maven - [Install Maven](https://maven.apache.org/install.html)
* Docker - [Install Docker community edition](https://hub.docker.com/search/?type=edition&offering=community)

To build and deploy your application for the first time, run the following in your shell:

```bash
idempotency-apigw$ sam build
idempotency-apigw$ sam deploy --guided
```

The first command will build the source of your application. The second command will package and deploy your application to AWS, with a series of prompts:

* **Stack Name**: The name of the stack to deploy to CloudFormation. This should be unique to your account and region, and a good starting point would be something matching your project name.
* **AWS Region**: The AWS region you want to deploy your app to.
* **Confirm changes before deploy**: If set to yes, any change sets will be shown to you before execution for manual review. If set to no, the AWS SAM CLI will automatically deploy application changes.
* **Allow SAM CLI IAM role creation**: Many AWS SAM templates, including this example, create AWS IAM roles required for the AWS Lambda function(s) included to access AWS services. By default, these are scoped down to minimum required permissions. To deploy an AWS CloudFormation stack which creates or modified IAM roles, the `CAPABILITY_IAM` value for `capabilities` must be provided. If permission isn't provided through this prompt, to deploy this example you must explicitly pass `--capabilities CAPABILITY_IAM` to the `sam deploy` command.
* **Save arguments to samconfig.toml**: If set to yes, your choices will be saved to a configuration file inside the project, so that in the future you can just re-run `sam deploy` without parameters to deploy changes to your application.

You can find your API Gateway Endpoint URL in the output values displayed after deployment.

## Testing

The SAM CLI reads the application template to determine the API routes and the functions that they invoke. The `Events` property on each function's definition includes the route and method for each path.

```yaml
    Events:
      CreateOrder:
        Type: Api # More info about API Event Source: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#api
        Properties:
          Path: /orders
          Method: post
          RestApiId: !Ref OrdersApi
```

The idempotent lambda will handle a POST request send to the /orders endpoint of the deployed API.
Having API Gateway Endpoint URL you can run the following command to call it:

```bash
curl -X POST <your-api-gateway-endpoint-url> -H "Content-Type: application/json" -d '{
    "userId": "user1",
    "items": [
        {
        "productId": "product1",
        "price": 6.50,
        "quantity": 5
        },
        {
        "productId": "product2",
        "price": 13.50,
        "quantity": 2
        }
    ],
    "comment": "AWSome Order"
}'  
```
You should get an orderId as a result. If you retry the call with the same payload, the returned orderId will be the same (by default for 1 hour but the setting can be changed).

You can notice that the idempotency key for the example has been configured as below:
```
  IdempotencyConfig.builder()
        .withEventKeyJMESPath("powertools_json(body).[requestToken,userId,items]")
        .build())
```
It means the following:
* comment field is not part of the idempotency key so changing the value of the comment will not cause creation of a new order
* there is another optional field  - `requestToken`, it allows the API consumer to send a client side generated token. It can be used for supporting the case of buying the same products by the same user before an expiration of the idempotency key.

## Add a resource to your application
The application template uses AWS Serverless Application Model (AWS SAM) to define application resources. AWS SAM is an extension of AWS CloudFormation with a simpler syntax for configuring common serverless application resources such as functions, triggers, and APIs. For resources not included in [the SAM specification](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md), you can use standard [AWS CloudFormation](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-template-resource-type-ref.html) resource types.

## Cleanup

To delete the sample application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
aws cloudformation delete-stack --stack-name <Name-of-your-deployed-stack>
```

# Appendix

## Powertools

**Tracing**

[Tracing utility](https://awslabs.github.io/aws-lambda-powertools-java/core/tracing/) provides functionality to reduce the overhead of performing common tracing tasks. It traces the execution of this sample code including the response and exceptions as tracing metadata - You can visualize them in AWS X-Ray.

**Logger**

[Logging utility](https://awslabs.github.io/aws-lambda-powertools-java/core/logging/) creates an opinionated application Logger with structured logging as the output, dynamically samples a percentage (samplingRate) of your logs in DEBUG mode for concurrent invocations, log incoming events as your function is invoked, and injects key information from Lambda context object into your Logger - You can visualize them in Amazon CloudWatch Logs.

**Metrics**

[Metrics utility](https://awslabs.github.io/aws-lambda-powertools-java/core/metrics/) captures cold start metric of your Lambda invocation, and could add additional metrics to help you understand your application KPIs - You can visualize them in Amazon CloudWatch.

## Resources

See the [AWS SAM developer guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) for an introduction to SAM specification, the SAM CLI, and serverless application concepts.

Check the [AWS Lambda Powertools Java](https://awslabs.github.io/aws-lambda-powertools-java/) for more information on how to use and configure such tools

Next, you can use AWS Serverless Application Repository to deploy ready to use Apps that go beyond simple examples and learn how authors developed their applications: [AWS Serverless Application Repository main page](https://aws.amazon.com/serverless/serverlessrepo/)