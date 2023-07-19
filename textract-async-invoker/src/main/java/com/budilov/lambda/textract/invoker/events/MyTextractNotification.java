package com.budilov.lambda.textract.invoker.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MyTextractNotification {
    @JsonProperty("Type")
    public String type;
    @JsonProperty("MessageId")
    public String messageId;
    @JsonProperty("TopicArn")
    public String topicArn;
    @JsonProperty("Message")
    public String message;
    @JsonProperty("Timestamp")
    public String timestamp;
    @JsonProperty("SignatureVersion")
    public String signatureVersion;
    @JsonProperty("Signature")
    public String signature;
    @JsonProperty("SigningCertURL")
    public String signingCertUrl;
    @JsonProperty("UnsubscribeURL")
    public String unsubscribeUrl;

    public static class MessageBody {

        public MessageBody() {}
        public static class DocumentLocation {
            @JsonProperty("S3ObjectName")
            public String s3ObjectName;
            @JsonProperty("S3Bucket")
            public String s3Bucket;
        }

        @JsonProperty("JobId")
        public String jobId;

        @JsonProperty("Status")
        public String status;

        @JsonProperty("API")
        public String api;

        @JsonProperty("JobTag")
        public String jobTag;

        @JsonProperty("Timestamp")
        public String timestamp;

        @JsonProperty("DocumentLocation")
        public DocumentLocation documentLocation;
    }

}
