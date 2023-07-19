package com.budilov.lambda.textract.invoker;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Vladimir Budilov
 */
public class AppProperties {

    public static String DIGITAL_ASSETS_UPLOADS_BUCKET;
    public static String REGION;
    public static String ACCOUNT_NUMBER;

    public static String TEXTRACT_SNS_ARN;
    public static String TEXTRACT_IAM_ROLE_ARN;
    public static String DYNAMO_DB_TABLE;


    /**
     * Let's initialize the variables from the application.properties file
     */
    static {

        Properties prop = new Properties();
        try {
            prop.load(AppProperties.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DIGITAL_ASSETS_UPLOADS_BUCKET = prop.getProperty("aws.bucket.digitalassetsuploads");
        REGION = prop.getProperty("aws.region") != null ? prop.getProperty("aws.region") : "us-east-1";
        ACCOUNT_NUMBER = prop.getProperty("aws.accountnumber");

        TEXTRACT_SNS_ARN = System.getenv("TEXTRACT_SNS_ARN");
        TEXTRACT_IAM_ROLE_ARN = System.getenv("TEXTRACT_IAM_ROLE_ARN");
        DYNAMO_DB_TABLE = System.getenv("DYNAMO_DB_TABLE");
    }

}
