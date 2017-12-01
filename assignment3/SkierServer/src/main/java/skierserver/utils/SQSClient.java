package skierserver.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class SQSClient {
    private static SQSClient sqsClient;
    private AmazonSQS sqs;

    private SQSClient() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(System.getenv("accessKey"), System.getenv("secretKey"));

        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.US_WEST_2)
                .build();

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SQSClient");
        System.out.println("===========================================\n");
    }

    public static SQSClient getInstance() {
        if (sqsClient == null) {
            sqsClient = new SQSClient();
        }

        return sqsClient;
    }

    public AmazonSQS getSQS() {
        return this.sqs;
    }
}
