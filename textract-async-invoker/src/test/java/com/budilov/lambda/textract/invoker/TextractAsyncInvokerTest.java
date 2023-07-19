package com.budilov.lambda.textract.invoker;

import com.budilov.lambda.textract.invoker.events.MyS3EventNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;

class TextractAsyncInvokerTest {
    private static final Logger log = Logger.getLogger(TextractAsyncInvokerTest.class);

    @Test()
    public void testS3EventParsing() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        URL url = TextractAsyncInvokerTest.class.getClassLoader().getResource("s3event.json");
        File file = Paths.get(url.toURI()).toFile();

        BufferedReader br
                = new BufferedReader(new FileReader(file));

        String st;
        StringBuffer buf = new StringBuffer();
        while ((st = br.readLine()) != null)
            buf.append(st);

        MyS3EventNotification eventNotification;
        eventNotification = mapper.readValue(buf.toString(), MyS3EventNotification.class);// gson.fromJson(buf.toString(), MyS3EventNotification.class);

        Assertions.assertNotNull(eventNotification, "eventNotification is not null");
        Assertions.assertNotNull(eventNotification.records[0].s3.object.key, "eventNotification has a valid key");

    }

}
