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


public class S3DataLakeStack extends Stack {


    public static Bucket datalakeBucket;

    public S3DataLakeStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public S3DataLakeStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        datalakeBucket = Bucket.Builder.create(this, AppProperties.APP_PEEFIX + "datalakeBucket")
                .bucketName(AppProperties.S3_DATALAKE_BUCKET)
                .removalPolicy(RemovalPolicy.RETAIN)
                .build();

        StringParameter.Builder.create(this, "DatalakeBucketName")
                .allowedPattern(".*")
                .description("datalakeBucketName")
                .parameterName("datalakeBucketName")
                .stringValue(datalakeBucket.getBucketName())
                .tier(ParameterTier.STANDARD)
                .build();

    }

}
