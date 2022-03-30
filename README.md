# Lambda Power Tools for Java - Idempotency Module

This project contains examples of AWS Lambda functions using idempotency module of AWS Lambda Powertools for Java.

For more information on the module, refer to the [documentation](https://awslabs.github.io/aws-lambda-powertools-java/utilities/idempotency/).

## Project structure
Each example is located in a separate directory:
* idempotency-api-gw - idempotent Lambda function processing requests sent to the Amazon API Gateway
* idempotency-sqs - idempotent Lambda function processing records read from the Amazon SQS queue

## Deploy the sample applications
The samples are based on Serverless Application Model (SAM) and you can use the SAM Command Line Interface (SAM CLI) to build and deploy them to your AWS account.

To use the SAM CLI, you need the following tools.

* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* Java11 - [Install the Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* Maven - [Install Maven](https://maven.apache.org/install.html)
* Docker - [Install Docker community edition](https://hub.docker.com/search/?type=edition&offering=community)

To build and deploy your application for the first time, change the directory to the example you are interested in and run the following commands in your shell.

```bash
$ sam build
$ sam deploy --guided
```

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.

