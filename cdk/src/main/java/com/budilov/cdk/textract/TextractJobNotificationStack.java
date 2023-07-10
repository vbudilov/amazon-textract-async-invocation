package com.budilov.cdk.textract;

import com.budilov.cdk.textract.util.AppProperties;
import com.budilov.cdk.textract.util.SqsUtil;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.ssm.ParameterTier;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.Collections;


public class TextractJobNotificationStack extends Stack {

    private static String mainStackPrefix = "TextractJobNotifications";

    public TextractJobNotificationStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public TextractJobNotificationStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Create the SNS Topic and add the value to SSM
        Topic textractJobNotificationSNSTopic = Topic.Builder.create(this, mainStackPrefix + "Topic")
                .topicName(AppProperties.TEXTRACT_NOTIFICATIONS_SNS_NAME)
                .build();
        StringParameter.Builder.create(this, mainStackPrefix + "SnsSSM")
                .allowedPattern(".*")
                .description(mainStackPrefix + "Topic")
                .parameterName(AppProperties.TEXTRACT_SSM_SNS_ARN)
                .stringValue(textractJobNotificationSNSTopic.getTopicArn())
                .tier(ParameterTier.STANDARD)
                .build();

        // Create the SQS Topic and add the value to SSM
        Queue queue = SqsUtil.createSqsQueue(this, mainStackPrefix + "Queue",
                AppProperties.TEXTRACT_NOTIFICATIONS_SQS_NAME,
                4, 120, mainStackPrefix + "QueueDLQ",
                AppProperties.TEXTRACT_NOTIFICATIONS_SQS_NAME_DLQ);
        StringParameter.Builder.create(this, mainStackPrefix + "SqsARNSSM")
                .allowedPattern(".*")
                .description(mainStackPrefix + "SQSARN")
                .parameterName(AppProperties.TEXTRACT_SSM_SQS_ARN)
                .stringValue(queue.getQueueArn())
                .tier(ParameterTier.STANDARD)
                .build();
        StringParameter.Builder.create(this, mainStackPrefix + "SqsSSM")
                .allowedPattern(".*")
                .description(mainStackPrefix + "SQS")
                .parameterName(AppProperties.TEXTRACT_SSM_SQS)
                .stringValue(queue.getQueueName())
                .tier(ParameterTier.STANDARD)
                .build();


        // Subscribe the SQS to SNS
        textractJobNotificationSNSTopic.addSubscription(new SqsSubscription(queue));


        // Create IAM Role that will be used by Textract to send notifications to SNS
        Role lambdaRole = Role.Builder.create(this, mainStackPrefix + "Role")
                .assumedBy(new ServicePrincipal("textract.amazonaws.com"))
                .description("Textract Service Role for Textract Job Notifications Lambda Function")
                .roleName("TextractServiceRoleLambdaFunctionRole")
                .build();
        // Allow access to S3
        lambdaRole.addToPolicy(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(Collections.singletonList("sns:Publish"))
                .resources(Collections.singletonList(textractJobNotificationSNSTopic.getTopicArn()))
                .build());

        // Add the role to SSM
        StringParameter.Builder.create(this, mainStackPrefix + "RoleSSM")
                .allowedPattern(".*")
                .description(mainStackPrefix + "TextractServiceRole")
                .parameterName(AppProperties.TEXTRACT_SSM_ROLE_ARN)
                .stringValue(lambdaRole.getRoleArn())
                .tier(ParameterTier.STANDARD)
                .build();
    }

}
