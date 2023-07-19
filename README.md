# Analyze Financial Docs with Amazon Textract

## Summary
This project will demonstrate how you can use [Amazon Textract](https://aws.amazon.com/textract/) to extract valuable 
and relevant financial info from financial documents in an asynchronous manner. 

## Architecture

![Alt text](images/amazon_textract_async_invoke.png?raw=true "Amazon Textract")


## Projects
There are 2 main projects in this folder:
1. cdk: your IaC (Infrastructure as Code)
2. textract-async-invoker (Lambda functions -- the guts of the business logic)

### cdk

You'll use the CDK project to setup your AWS infrastructure. If you're not familiar with what [AWS Cloud Development Kit](https://aws.amazon.com/cdk/) is
I recommend you spend an hour or two experimenting with it since it'll save you a ton of time deploying, customizing, 
redeploying, testing, and fixing/debugging the provisioned infrastructure.

### textract-async-invoker (Lambda functions)
There are 2 Lambda functions: 
1. TextractAsyncInvoker
2. TextractJobIdHandler

The first one submits the Textract job and the second one receives the result of the job

## Deployment

### cdk

1. Change the property values under cdk/src/main/resources/application.properties

Enter your correct account number --> aws.accountnumber=XXXXXXXXX

Modify any other variable value with the names that you'd like to use...if the deployment fails that might mean the S3 bucket 
name has been taken already so you might need to change it (the Exceptions are quite explicit so just read through them)

2. Build the `cdk` project (go to the cdk folder and type ```bash mvn package && cdk synth```). Once you do that you'll see the following line:
```Supply a stack id (S3UploadsBucketStack, TextractJobNotificationsStack, S3DataLakeBucketStack, DDBStack) to display its template.```

3. To deploy all of the resources that were just complied type ```cdk deploy \*```, otherwise deploy each one of them 
independenlty by typing out their names ```e.g. cdk deploy "S3UploadsBucketStacK"```
During the deployment you will be prompted multiple times to type in 'y' to proceed with the installation process so 
you'll need to be watching the installation (most of the prompts are to confirm that you want to create the appropriate IAM 
roles) 
4. [Optional] If you want to delete everything (some resources will not be removed though) then type ```cdk destroy \*``` and follow the prompts

Voila! Your infrastructure is provisioned! Now you're ready to compile and deploy your Lambda functions 

### textract-async-invoker (Lambda functions)
1. Change the account and region property value under textract-async-invoker/src/main/resources/application.properties

Enter your correct account number --> aws.accountnumber=XXXXXXXXX

2. Build the project

```./gradlew fatJar```

3. Deploy the Lambda functions

```serverless deploy```
