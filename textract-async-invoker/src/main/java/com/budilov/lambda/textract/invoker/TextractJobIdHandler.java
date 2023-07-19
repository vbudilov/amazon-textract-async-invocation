package com.budilov.lambda.textract.invoker;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.budilov.lambda.textract.invoker.events.MyTextractNotification;
import com.budilov.lambda.textract.invoker.service.TextractExpenseAsyncService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import software.amazon.awssdk.services.textract.model.ExpenseDocument;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This Lambda function is responsible for getting the result from the Textract Service
 * <p>
 * 1. Get the SQS event and
 *
 * @author Vladimir Budilov
 */
public class TextractJobIdHandler implements RequestHandler<SQSEvent, Void> {

    private static final Logger log = Logger.getLogger(TextractJobIdHandler.class);
    private static final TextractExpenseAsyncService textractExpenseAsyncService = new TextractExpenseAsyncService();
    ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        try {
            log.info("rawInput: " + mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Couldn't write out the raw message ", e);

            throw new RuntimeException(e);
        }

        for (SQSEvent.SQSMessage record : event.getRecords()) {
            MyTextractNotification myTextractNotification;
            try {
                myTextractNotification = mapper.readValue(record.getBody(), MyTextractNotification.class);
            } catch (JsonProcessingException e) {
                log.error("Couldn't create the MyTextractNotification object", e);
                throw new RuntimeException("Couldn't create the MyTextractNotification object", e);
            }
            MyTextractNotification.MessageBody messageBody;
            try {
                 messageBody = mapper.readValue(myTextractNotification.message, MyTextractNotification.MessageBody.class);
            } catch (JsonProcessingException e) {
                log.error("Couldn't deserialize the MyTextractNotification.MessageBody object", e);
                throw new RuntimeException("Couldn't deserialize the MyTextractNotification.MessageBody object",e);
            }

            log.info("Get the Textract result based on jobId");

            // Get the Textract result based on jobId
            List<ExpenseDocument> expenseDocumentList = textractExpenseAsyncService
                    .getJobResults(messageBody.jobId);

            log.info("expenseDocumentList: " + expenseDocumentList);

            // todo: update the job id in DDB

        }

        return null;
    }


}
