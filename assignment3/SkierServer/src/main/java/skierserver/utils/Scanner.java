package skierserver.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import skierserver.dao.SkiDao;
import skierserver.model.SkierData;


import javax.servlet.ServletContext;
import java.util.List;

public class Scanner implements Runnable {
    private ServletContext context;
    private String sqsUrl;

    public Scanner(ServletContext context) {
        this.context = context;
        this.sqsUrl = "https://sqs.us-west-2.amazonaws.com/358115688307/CacheQueue";
    }

    @Override
    public void run() {

        SQSClient sqsClient = (SQSClient) context.getAttribute("sqsClient");

        AmazonSQS sqs = sqsClient.getSQS();

        // get approximate number of visible messages in a queue
        // if there's no messages in the queue, return
        Map<String, String> attributes = getQueueAttributes(sqs);
        int messageSize = Integer.parseInt(attributes.get("ApproximateNumberOfMessages"));

        if (messageSize == 0) {
            return;
        }

        System.out.println("begin retrive messgaes");

        List<SkierData> skierDataList = new ArrayList<>();

        try {
            for (int i = 0; i < 500; i++) {
                ReceiveMessageResult result;
                List<Message> messages;

                // batch get
                result = sqs.receiveMessage(new ReceiveMessageRequest(sqsUrl).withMaxNumberOfMessages(10));

                if (!result.getMessages().isEmpty()) {
                    messages = result.getMessages();

                    DeleteMessageBatchRequest batchRequest = new DeleteMessageBatchRequest().withQueueUrl(sqsUrl);
                    List<DeleteMessageBatchRequestEntry> entries = new ArrayList<>();

                    for (int j = 0; j < messages.size(); j++) {
                        SkierData skierData = new SkierData();
                        skierData = skierData.jsonToObj(messages.get(j).getBody());
                        skierDataList.add(skierData);
                        entries.add(new DeleteMessageBatchRequestEntry().withId(Integer.toString(j))
                                .withReceiptHandle(messages.get(j)
                                        .getReceiptHandle()));
                    }

                    // batch delete
                    batchRequest.setEntries(entries);
                    DeleteMessageBatchResult batchResult = sqs.deleteMessageBatch(batchRequest);

                    // redelete the failed ones
                    if (!batchResult.getFailed().isEmpty()) {
                        System.out.println("redeleted the failed ones: " + batchResult.getFailed().size());
                        int n = batchResult.getFailed().size();

                        for (BatchResultErrorEntry e : batchResult.getFailed()) {
                            sqs.deleteMessage((new DeleteMessageRequest(sqsUrl,
                                    messages.get(Integer.parseInt(e.getId()))
                                            .getReceiptHandle())));
                        }
                    }
                }
            }
        } catch (AmazonClientException e) {
            e.printStackTrace();
            System.out.println("Error Message: " + e.getMessage());
        }

        System.out.println("begin batch " + skierDataList.size());

        int failedCount = 0;
        if (skierDataList.size() > 0) {
            failedCount = dbBatchInsert(skierDataList);
        }

        if (failedCount > 0) {
            String failedQueueUrl = "https://sqs.us-west-2.amazonaws.com/358115688307/FailedQueue";
            sqs.sendMessage(new SendMessageRequest(failedQueueUrl, String.valueOf(failedCount) + ",3"));
        }
    }

    private Map<String, String> getQueueAttributes(AmazonSQS sqs) {
        GetQueueAttributesRequest queueAttributesRequest =
                new GetQueueAttributesRequest(sqsUrl).withAttributeNames("All");
        GetQueueAttributesResult queueAttributes = sqs.getQueueAttributes(queueAttributesRequest);
        return queueAttributes.getAttributes();
    }

    private int dbBatchInsert(List<SkierData> skierDataList) {
        int failedCount = 0;
        SkiDao skiDao = new SkiDao();
        try {
            failedCount = skiDao.bacthInsert(skierDataList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return failedCount;

    }

}
