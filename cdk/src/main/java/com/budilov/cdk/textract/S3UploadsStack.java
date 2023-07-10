package com.budilov.cdk.textract;

import com.budilov.cdk.textract.util.AppProperties;
import com.budilov.cdk.textract.util.SqsUtil;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.ssm.ParameterTier;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;


public class S3UploadsStack extends Stack {


    public static Bucket uploadsBucket;

    public S3UploadsStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public S3UploadsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        uploadsBucket = Bucket.Builder.create(this, AppProperties.APP_PEEFIX + "uploadsBucket")
                .bucketName(AppProperties.S3_UPLOADS_BUCKET)
                .removalPolicy(RemovalPolicy.RETAIN)
                .build();


        StringParameter.Builder.create(this, "UploadsBucket")
                .allowedPattern(".*")
                .description("uploadsBucketNotificationName")
                .parameterName("uploadsBucketNotificationName")
                .stringValue(uploadsBucket.getBucketName())
                .tier(ParameterTier.STANDARD)
                .build();

        StringParameter.Builder.create(this, "UploadsBucketNotificationARN")
                .allowedPattern(".*")
                .description("uploadsBucketNotificationARN")
                .parameterName("uploadsBucketNotificationARN")
                .stringValue(uploadsBucket.getBucketArn())
                .tier(ParameterTier.STANDARD)
                .build();

        SqsUtil.createS3Flow(this, "uploadsBucket", AppProperties.S3_UPLOADS_NOTIFICATION_SQS,
                4, "uploadsBucketDLQ", AppProperties.S3_UPLOADS_NOTIFICATION_SQS_DLQ,
                "uploadsBucketNotificationSQS",  null, 120, uploadsBucket);


    }

}
