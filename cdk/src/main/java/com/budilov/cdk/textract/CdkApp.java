package com.budilov.cdk.textract;

import software.amazon.awscdk.App;

public class CdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new S3UploadsStack(app, "S3UploadsBucketStack");
        new TextractJobNotificationStack(app, "TextractJobNotificationsStack");
        new S3DataLakeStack(app, "S3DataLakeBucketStack");
        new DDBTableStack(app, "DDBStack");

        app.synth();
    }
}

