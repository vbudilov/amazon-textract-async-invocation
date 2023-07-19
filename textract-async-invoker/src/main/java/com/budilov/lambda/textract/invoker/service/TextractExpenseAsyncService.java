package com.budilov.lambda.textract.invoker.service;

import com.budilov.lambda.textract.invoker.AppProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.textract.TextractAsyncClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 *
 *
 * @author Vladimir Budilov
 */
public class TextractExpenseAsyncService {

    private static final Logger log = LogManager.getLogger("TextractExpenseAsyncService");

    private static final TextractAsyncClient textractClient = TextractAsyncClient.create();

    private static final TextractExpenseAsyncService textractExpenseAsyncService = new TextractExpenseAsyncService();


    public static void main(String[] args) throws Exception {
//        Configurator.setRootLevel(Level.INFO);
        String bucket = "budilovv.textract";
        List<String> receiptNames = new ArrayList<>();
        receiptNames.add("IMG_8402.jpeg");
        receiptNames.add("IMG_7934.jpeg");
        receiptNames.add("IMG_7857.jpeg");
        receiptNames.add("IMG_8735.jpeg");

        log.info("starting: ");


        String jobId = null;
        log.info("initialized: ");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            jobId = textractExpenseAsyncService.startExpenseAnalysis(bucket, receiptNames.get(3), null);

            List<ExpenseDocument> expenseDocumentList = textractExpenseAsyncService
                    .getJobResults(jobId);
            log.info("# of docs: " + expenseDocumentList.size());
            expenseDocumentList.get(0).summaryFields().forEach(summaryField -> {
                log.info(summaryField.type().text() + " -> " + summaryField.valueDetection().text());
            });
            expenseDocumentList.get(0).lineItemGroups().forEach(lineItemGroup -> {
                log.info("LineItemGroup #" + lineItemGroup.lineItems().size());
                lineItemGroup.lineItems().forEach(lineItem -> {
                    log.info("\nLineItem");
                    lineItem.lineItemExpenseFields().forEach(expenseField -> {
                        log.info(expenseField.type().text() + " -> "
                                + expenseField.valueDetection().text());

                    });
                });
            });
//            String json = gson.toJson(expenseDocumentList.get(0).lineItemGroups());
//            log.info(json);
//            expenseDocumentList.forEach( expenseDocument -> {
////                log.info(expenseDocument);
//
//                log.info(expenseDocument.summaryFields());
//                log.info(expenseDocument.lineItemGroups());
//
//            });
        } catch (Exception exc) {
            log.error(exc);
        }

        log.info("jobID: " + jobId);
    }

    /**
     * Kicks off the async expense analysis
     *
     * @param bucketName
     * @param docName
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public String startExpenseAnalysis(String bucketName, String docName, Boolean snsNotification) throws ExecutionException, InterruptedException {
        String jobId = null;

        try {
            List<FeatureType> myList = new ArrayList<>();
            myList.add(FeatureType.TABLES);
            myList.add(FeatureType.FORMS);

            // Define the S3 object location
            S3Object s3Object = S3Object.builder()
                    .bucket(bucketName)
                    .name(docName)
                    .build();
            DocumentLocation location = DocumentLocation.builder()
                    .s3Object(s3Object)
                    .build();

            // Define the expense analysis job
            StartExpenseAnalysisRequest documentAnalysisRequest = StartExpenseAnalysisRequest.builder()
                    .documentLocation(location)
                    .notificationChannel(NotificationChannel.builder()
                            .roleArn(AppProperties.TEXTRACT_IAM_ROLE_ARN) // AIM role needed to send the message to an SNS
                            .snsTopicArn(AppProperties.TEXTRACT_SNS_ARN) // SNS topic to invoke after the job is done
                            .build())
                    .build();

            log.info("Kicking off the async Textract job");
            // Kick off the job
            CompletableFuture<StartExpenseAnalysisResponse> response = textractClient.startExpenseAnalysis(documentAnalysisRequest);

            // Get the job ID
            jobId = response.get().jobId();

            log.info("Initiated the async Textract job with jobId: " + jobId);
        } catch (TextractException | ExecutionException | InterruptedException e) {
            log.error("Could not kick off the expense analysis job", e);
            throw e;
        }

        return jobId;
    }

    /**
     * Receive the processed expense document
     *
     * @param jobId
     * @return
     */
    public List<ExpenseDocument> getJobResults(String jobId) {

        boolean finished = false;
        int index = 0;
        String status = "";

        List<ExpenseDocument> expenseDocumentList = null;
        try {
            while (!finished) {
                GetExpenseAnalysisRequest analysisRequest = GetExpenseAnalysisRequest.builder()
                        .jobId(jobId)
                        .maxResults(1000)
                        .build();

                CompletableFuture<GetExpenseAnalysisResponse> response = textractClient.getExpenseAnalysis(analysisRequest);
                status = response.get().jobStatus().toString();
                expenseDocumentList = response.get().expenseDocuments();
                if (status.compareTo("SUCCEEDED") == 0)
                    finished = true;
                else {
                    log.info(index + " status is: " + status);
                    Thread.sleep(5000);
                }
                index++;
            }

            return expenseDocumentList;

        } catch (InterruptedException e) {
            log.error(e.getMessage());
            System.exit(1);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}



