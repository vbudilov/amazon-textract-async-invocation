package com.budilov.lambda.textract.invoker.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyS3EventNotification {

    @JsonProperty("Records")
    public Record[] records;

    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class Bucket {
        public String name;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class MyS3Object {
        public String key;
        public int size;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class OwnerIdentity {
        public String principalId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Record {
        public String awsRegion;
        public Date eventTime;
        public String eventName;
        public UserIdentity userIdentity;
        public RequestParameters requestParameters;
        public S3 s3;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class RequestParameters {
        public String sourceIPAddress;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)

    public static class S3 {
        public String s3SchemaVersion;
        public String configurationId;
        public Bucket bucket;
        public MyS3Object object;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserIdentity {
        public String principalId;
    }
}


