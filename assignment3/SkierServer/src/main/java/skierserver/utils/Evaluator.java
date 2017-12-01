package skierserver.utils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;

import java.util.*;

public class Evaluator {
    public static void main(String[] args) {
        String metricsQueueUrl = "https://sqs.us-west-2.amazonaws.com/358115688307/MetricsQueue";
        String errorQueueUrl = "https://sqs.us-west-2.amazonaws.com/358115688307/FailedQueue";
        List<List<Integer>> latencyList = getQueue(metricsQueueUrl);
        List<List<Integer>> errorList = getQueue(errorQueueUrl);

        List<Integer> latencyList0 = latencyList.get(0);
        List<Integer> latencyList1 = latencyList.get(1);
        List<Integer> latencyList2 = latencyList.get(2);
        List<Integer> latencyList3 = latencyList.get(3);

        List<Integer> errorList0 = errorList.get(0);
        List<Integer> errorList1 = errorList.get(1);
        List<Integer> errorList2 = errorList.get(2);
        List<Integer> errorList3 = errorList.get(3);


        System.out.println("------Metrics for three instances------\n");
        evaluate(latencyList0, 0);
        System.out.println("\n");

        System.out.println("---------Metrics for instance 1--------\n");
        evaluate(latencyList1, 0);
        System.out.println("\n");

        System.out.println("---------Metrics for instance 2--------\n");
        evaluate(latencyList2, 0);
        System.out.println("\n");

        System.out.println("---------Metrics for instance 3--------\n");
        evaluate(latencyList3, 0);
        System.out.println("\n");

    }

    private static void evaluate(List<Integer> latancyList, int errorNumber) {
        double mean = getMean(latancyList);
        int median = getMedian(latancyList);
        int nightyFivePerventile = getPercentile(latancyList, 0.95);
        int nightyNinePercentile = getPercentile(latancyList, 0.99);
        printMetrics(errorNumber, latancyList.size(), mean, median, nightyFivePerventile, nightyNinePercentile);
    }

    public static void printMetrics(int errorNumber, int totalRequests, double mean, int median, int nightyFivePercentile, int nightyNinePercentile) {
        System.out.println("[Total requests]:            " + totalRequests);
        System.out.println("[Error requests]:            " + errorNumber);
        System.out.println("[Latency - Median]:          " + mean + " ms");
        System.out.println("[Latency - Mean]:            " + median + " ms");
        System.out.println("[Latency - 95th Percentile]: " + nightyFivePercentile + " ms");
        System.out.println("[Latency - 99th Percentile]: " + nightyNinePercentile + " ms");
    }

    private static int getMedian(List<Integer> list) {
        int middle = list.size() / 2;

        if (list.size() % 2 == 1) {
            return list.get(middle);
        } else {
            return (list.get(middle - 1) + list.get(middle) / 2);
        }
    }

    private static double getMean(List<Integer> list) {
        int sum = 0;
        for (int l : list) {
            sum += l;
        }
        double mean = 1.0d * sum / list.size();

        return mean;
    }

    private static int getPercentile(List<Integer> list, double percentile) {
        double index = Math.round(list.size() * percentile);
        return list.get((int) index);
    }

    private static List<List<Integer>> getQueue(String metricsQueueUrl) {
        List<List<Integer>> list = new ArrayList<>();
        List<Integer> metricsList = new ArrayList<>();
        List<Integer> metricsList1 = new ArrayList<>();
        List<Integer> metricsList2 = new ArrayList<>();
        List<Integer> metricsList3 = new ArrayList<>();

        // get sqs
        SQSClient sqsClient = SQSClient.getInstance();
        AmazonSQS sqs = sqsClient.getSQS();
        ReceiveMessageResult result;
        List<Message> messages;

        GetQueueAttributesRequest queueAttributesRequest = new GetQueueAttributesRequest(metricsQueueUrl).withAttributeNames("All");
        GetQueueAttributesResult queueAttributes = sqs.getQueueAttributes(queueAttributesRequest);
        Map<String, String> attributes = queueAttributes.getAttributes();
        int queueLength = Integer.parseInt(attributes.get("ApproximateNumberOfMessages"));

        System.out.println(queueLength);

        try {
            for (int i = 0; i < 10000; i++) {
                // batch get
                result = sqs.receiveMessage(new ReceiveMessageRequest(metricsQueueUrl).withMaxNumberOfMessages(10));

                if (!result.getMessages().isEmpty()) {
                    messages = result.getMessages();
                    DeleteMessageBatchRequest batchRequest = new DeleteMessageBatchRequest().withQueueUrl(metricsQueueUrl);
                    List<DeleteMessageBatchRequestEntry> entries = new ArrayList<>();

                    for (int j = 0; j < messages.size(); j++) {
                        String message = messages.get(j).getBody();

                        String[] arr = message.split(",");
                        int latency = Integer.parseInt(arr[0]);
                        String instance = arr[1];

                        metricsList.add(latency);

                        if (instance.equals("1")) {
                            metricsList1.add(latency);
                        } else if (instance.equals("2")) {
                            metricsList2.add(latency);
                        } else {
                            metricsList3.add(latency);
                        }

                        entries.add(new DeleteMessageBatchRequestEntry().withId(Integer.toString(j))
                                .withReceiptHandle(messages.get(j)
                                        .getReceiptHandle()));
                    }

                    // batch delete
                    batchRequest.setEntries(entries);
                    DeleteMessageBatchResult batchResult = sqs.deleteMessageBatch(batchRequest);

                    // redelete the failed ones
                    if (!batchResult.getFailed().isEmpty()) {
                        int n = batchResult.getFailed().size();

                        for (BatchResultErrorEntry e : batchResult.getFailed()) {
                            sqs.deleteMessage((new DeleteMessageRequest(metricsQueueUrl,
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

        Collections.sort(metricsList);
        Collections.sort(metricsList1);
        Collections.sort(metricsList2);
        Collections.sort(metricsList3);

        list.add(metricsList);
        list.add(metricsList1);
        list.add(metricsList2);
        list.add(metricsList3);

        return list;
    }


}
