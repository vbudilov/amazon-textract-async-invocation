package com.budilov.lambda.textract.invoker;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.budilov.lambda.textract.invoker.events.MyS3EventNotification;
import com.budilov.lambda.textract.invoker.service.TextractExpenseAsyncService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

/**
 * This method does the following:
 * <p>
 * 1. Receives an SQS S3 Event from the SQS queue
 * <p>
 * 2. Calls the Textract API to analyze the receipt
 * <p>
 * 3. Record the jobId in DynamoDB table for tracking
 *
 * @author Vladimir Budilov
 */
public class TextractAsyncInvoker implements RequestHandler<SQSEvent, Void> {

    private static final Logger log = Logger.getLogger(TextractAsyncInvoker.class);
    private static final TextractExpenseAsyncService textractExpenseAsyncService = new TextractExpenseAsyncService();
    ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    @Override
    public Void handleRequest(SQSEvent input, Context context) {

        try {
            log.info("rawInput: " + mapper.writeValueAsString(input));
        } catch (JsonProcessingException e) {
            log.error("Couldn't serialize the input", e);
            throw new RuntimeException(e);
        }

        // Loop through SQS messages and process them one by one
        for (SQSEvent.SQSMessage message : input.getRecords()) {

            MyS3EventNotification eventNotification = null;
            String bucketName = null;
            String key = null;
            try {
                log.info("body: " + message.getBody());
                eventNotification = mapper.readValue(message.getBody(), MyS3EventNotification.class);

            } catch (Exception exc) {
                log.error("Exception caught", exc);
                throw new RuntimeException("Couldn't marshal the body into an S3Event...getting out");
            }


            key = URLDecoder.decode(eventNotification.records[0].s3.object.key, StandardCharsets.UTF_8);
            bucketName = eventNotification.records[0].s3.bucket.name;

            log.info("Key: " + key + " bucket: " + bucketName);

            // Call Textract and submit the async job
            String jobId;
            try {
                jobId = textractExpenseAsyncService.startExpenseAnalysis(bucketName, key, true);
            } catch (ExecutionException | InterruptedException e) {
                log.error("Couldn't submit job to Textract service", e);
                throw new RuntimeException(e);
            }
            log.info("Job submitted successfully. with job id: " + jobId);

            // todo: add the job id to DDB

        }


        return null;
    }
}


