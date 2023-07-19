package com.budilov.lambda.textract.invoker;

import com.budilov.lambda.textract.invoker.events.MyTextractNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;

class TextractJobIdHandlerTest {
    private static final Logger log = Logger.getLogger(TextractJobIdHandlerTest.class);

    @Test
    public void testTextractNotificationMarshalling() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        URL url = TextractAsyncInvokerTest.class.getClassLoader().getResource("textractJobIdNotification.json");
        assert url != null;
        File file = Paths.get(url.toURI()).toFile();

        BufferedReader br
                = new BufferedReader(new FileReader(file));

        String st;
        StringBuilder buf = new StringBuilder();
        while ((st = br.readLine()) != null)
            buf.append(st);

        MyTextractNotification textractNotification = mapper.readValue(buf.toString(), MyTextractNotification.class);// gson.fromJson(buf.toString(), MyS3EventNotification.class);

        log.info("message: " + textractNotification.message);
        Assertions.assertNotNull(textractNotification, "sqsEvent is not null");
        Assertions.assertNotNull(textractNotification.message, "message isn't null " + textractNotification.message);
        MyTextractNotification.MessageBody messageBody = mapper.readValue(textractNotification.message, MyTextractNotification.MessageBody.class);
        Assertions.assertNotNull(messageBody, "messageBody isn't null " + messageBody);
        Assertions.assertNotNull(messageBody.documentLocation.s3Bucket, "documentLocation.s3bucket isn't null " + messageBody);

    }
}
