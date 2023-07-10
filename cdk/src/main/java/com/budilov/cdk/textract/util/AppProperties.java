package com.budilov.cdk.textract.util;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class AppProperties {

    public static final String REGION;
    public static final String APP_PEEFIX;
    public static final String ACCOUNT_NUMBER;

    public static final String S3_DATALAKE_BUCKET;

    public static final String DDB_NAME;

    // S3 Uploads Bucket & queueing setup
    public static final String S3_UPLOADS_BUCKET;
    public static final String S3_UPLOADS_NOTIFICATION_SQS;
    public static final String S3_UPLOADS_NOTIFICATION_SQS_DLQ;


    public static final String TEXTRACT_SSM_SNS_ARN;
    public static final String TEXTRACT_SSM_SQS_ARN;
    public static final String TEXTRACT_SSM_SQS;
    public static final String TEXTRACT_SSM_ROLE_ARN;

    // Textract workflow notifications
    public static final String TEXTRACT_NOTIFICATIONS_SQS_NAME;
    public static final  String TEXTRACT_NOTIFICATIONS_SNS_NAME;
    public static final  String TEXTRACT_NOTIFICATIONS_SQS_NAME_DLQ;


    /**
     * Let's initialize the variables from the application.properties file
     */
    static {

        Properties prop = new Properties();
        try {
            prop.load(AppProperties.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't retrieve the application.properties file from the classpath. ", e);
        }


        REGION = Objects.requireNonNull(prop.getProperty("aws.region"), "AWS Region is required");
        APP_PEEFIX = Objects.requireNonNull(prop.getProperty("cdk.appprefix"), "CDK App Prefix is required");
        ACCOUNT_NUMBER = Objects.requireNonNull(prop.getProperty("aws.accountnumber"), "AWS Account Number is required");

        S3_DATALAKE_BUCKET= Objects.requireNonNull(prop.getProperty("aws.s3.datalake"),
                "S3 datalake Bucket is required");

        DDB_NAME= Objects.requireNonNull(prop.getProperty("aws.ddb.name"),
                "DDB table is required");
        // S3
        S3_UPLOADS_BUCKET = Objects.requireNonNull(prop.getProperty("aws.s3.uploads"),
                "S3 Uploads Bucket is required");
        S3_UPLOADS_NOTIFICATION_SQS = Objects.requireNonNull(prop.getProperty("aws.sqs.s3.notification-queue"),
                "S3 Uploads Notifications SQS is required");
        S3_UPLOADS_NOTIFICATION_SQS_DLQ = Objects.requireNonNull(prop.getProperty("aws.sqs.s3.notification-queue-dlq"),
                "S3 Uploads Notifications SQS is required");

        TEXTRACT_SSM_SNS_ARN = Objects.requireNonNull(prop.getProperty("aws.ssm.snsarn.textract"), "SSM SNS Name is required: aws.ssm.snsarn.textract");
        TEXTRACT_SSM_SQS_ARN = Objects.requireNonNull(prop.getProperty("aws.ssm.sqsarn.textract"), "SSM ARN SQS Name is required: aws.ssm.sqsarn.textract");
        TEXTRACT_SSM_SQS = Objects.requireNonNull(prop.getProperty("aws.ssm.sqs.textract"), "SSM SQS Name is required: aws.ssm.sqs.textract");
        TEXTRACT_SSM_ROLE_ARN = Objects.requireNonNull(prop.getProperty("aws.ssm.rolearn.textract"), "SSM Role Name is required: aws.ssm.rolearn.textract");

        TEXTRACT_NOTIFICATIONS_SQS_NAME = Objects.requireNonNull(prop.getProperty("aws.sqs.textract-notification"), "Textract Notifications SQS is required");
        TEXTRACT_NOTIFICATIONS_SNS_NAME = Objects.requireNonNull(prop.getProperty("aws.sns.textract-notification"), "Textract Notifications SNS is required");
        TEXTRACT_NOTIFICATIONS_SQS_NAME_DLQ = Objects.requireNonNull(prop.getProperty("aws.sqs.textract-notification-dlq"), "Textract Notifications DLQ is required");
    }
}
