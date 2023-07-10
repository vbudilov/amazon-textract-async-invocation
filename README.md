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
TBD (I'm cleaning up the code before I can open-source it so stay tuned)

## Deployment

### cdk

1. Change the property values under cdk/src/main/resources/application.properties

Enter your correct account number --> aws.accountnumber=XXXXXXXXX

Modify any other variable value with the names that you'd like to use...if the deployment fails that might mean the S3 bucket 
name has been taken already so you might need to change it (the Exceptions are quite explicit so just read through them)

2. Build the `cdk` project (go to the cdk folder and type ```bash mvn package && cdk synth```). Once you do that you'll see the following line:
```Supply a stack id (S3UploadsBucketStack, TextractJobNotificationsStack, S3DataLakeBucketStack, DDBStack) to display its template.```

3. To deploy all of the resources that were just complied type ```cdk deploy *```, otherwise deploy each one of them 
independenlty by typing out their names ```e.g. cdk deploy "S3UploadsBucketStacK"```

Voila! Your infrastructure is provisioned! Now you're ready to compile and deploy your Lambda functions 

### textract-async-invoker (Lambda functions)
TBD (I'm cleaning up the code before I can open-source it so stay tuned)

