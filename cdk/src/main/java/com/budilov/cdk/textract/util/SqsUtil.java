package com.budilov.cdk.textract.util;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.NotificationKeyFilter;
import software.amazon.awscdk.services.s3.notifications.SqsDestination;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.ssm.ParameterTier;
import software.amazon.awscdk.services.ssm.StringParameter;

public class SqsUtil {

    /**
     * This method creates the required SQS queue as well as the S3 notification.
     * After the queue is created an SSM parameter is stored for further retrieval.
     *
     * @param stack
     * @param id
     * @param queueName
     * @param retentionPeriod
     * @param dlqId
     * @param dlqQueueName
     * @param ssmQueueParameterName
     * @param visibilityTimeoutSeconds
     * @param bucket                   - could be null
     */
    public static void createS3Flow(Stack stack, String id, String queueName, Number retentionPeriod, String dlqId,
                                    String dlqQueueName,
                                    String ssmQueueParameterName, String notificationFilter,
                                    Number visibilityTimeoutSeconds, Bucket bucket) {

        // Create queue
        Queue queue = createSqsQueue(stack, id, queueName, retentionPeriod,
                visibilityTimeoutSeconds, dlqId, dlqQueueName);

        // Create SSM Parameter for the s3 bucket name and populate it
        createSSMParameter(stack, ssmQueueParameterName, queue.getQueueName());
        createSSMParameter(stack, ssmQueueParameterName+"ARN", queue.getQueueArn());

        // Setup the bucket notification if bucket was passed in
        if (bucket != null) {
            SqsDestination structuredQueueDestination = new SqsDestination(queue);
            if (notificationFilter != null)
                bucket.addEventNotification(EventType.OBJECT_CREATED,
                        structuredQueueDestination, NotificationKeyFilter.builder()
                                .prefix(notificationFilter)
                                .build());
            else bucket.addEventNotification(EventType.OBJECT_CREATED,
                    structuredQueueDestination);
        }

    }

    /**
     * Create SSM parameter and populate it
     *
     * @param stack
     * @param ssmParameterName
     * @param queueName
     */
    private static void createSSMParameter(Stack stack, String ssmParameterName, String queueName) {
        // Store the queue name in Parameter Store
        StringParameter.Builder.create(stack, ssmParameterName + "Queue")
                .allowedPattern(".*")
                .description(ssmParameterName + "Queue")
                .parameterName(ssmParameterName + "Queue")
                .stringValue(queueName)
                .tier(ParameterTier.STANDARD)
                .build();
    }

    /**
     * Creates the queue
     *
     * @param stack
     * @param id
     * @param queueName
     * @param retentionPeriod
     * @param visibilityTimeoutSeconds
     * @param dlqId
     * @param dlqQueueName
     * @return
     */
    public static Queue createSqsQueue(Stack stack, String id, String queueName,
                                       Number retentionPeriod, Number visibilityTimeoutSeconds,
                                       String dlqId, String dlqQueueName) {
        return Queue.Builder.create(stack, id)
                .queueName(queueName)
                .retentionPeriod(Duration.days(retentionPeriod))
                .visibilityTimeout(Duration.seconds(visibilityTimeoutSeconds))
                .deadLetterQueue(DeadLetterQueue.builder()
                        .maxReceiveCount(5)
                        .queue(Queue.Builder.create(stack, dlqId)
                                .queueName(dlqQueueName).build())
                        .build())
                .build();

    }
}
