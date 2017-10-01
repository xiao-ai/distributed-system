import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class ThreadTester {
    public List<Future<Result>> runThread(int iteration, int threadCount, WebTarget webTarget) {
        System.out.println(threadCount + " threads, " + iteration + " iterations");

        List<Callable<Result>> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            tasks.add(new HttpRequest(iteration, webTarget));
        }

        List<Future<Result>> result = null;

        long start = System.currentTimeMillis();
        try {
            System.out.println("Running threads...");
            result = executor.invokeAll(tasks);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        long end = System.currentTimeMillis();

        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished, total run time: " + (end - start) / 1000 + " seconds");

        return result;

    }

    public long calculateMedian(List<Long> list) {
        int middle = list.size() / 2;
        if (list.size() % 2 == 1) {
            return list.get(middle);
        } else {
            return (list.get(middle - 1) + list.get(middle) / 2);
        }
    }

    public double calculateAverage(List<Long> list) {
        int sum = 0;
        for (long l : list)
            sum += l;
        double average = 1.0d * sum / list.size();

        return average;
    }

    public long calculatePercentile(List<Long> list, double percentile) {
        double index = Math.round(list.size() * percentile);
        return list.get((int) index);
    }

    public void evaluate(List<Future<Result>> results) {
        int successPostRequests = 0;
        int successGetRequests = 0;
        List<Long> getLatencyList = new ArrayList<>();
        List<Long> postLatencyList = new ArrayList<>();

        for (Future<Result> future : results) {
            try {
                Result result = future.get();
                successGetRequests += result.getGetSuccessCount();
                successPostRequests += result.getPostSuccessCount();
                getLatencyList.addAll(result.getGetLatencies());
                postLatencyList.addAll(result.getPostLatencies());

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        List<Long> latencyList = new ArrayList<>();
        latencyList.addAll(getLatencyList);
        latencyList.addAll(getLatencyList);

        Collections.sort(getLatencyList);
        Collections.sort(postLatencyList);
        Collections.sort(latencyList);

        double getMedian = calculateMedian(getLatencyList);
        double postMedian = calculateMedian(postLatencyList);
        double median = calculateMedian(latencyList);

        double getMean = calculateAverage(getLatencyList);
        double postMean = calculateAverage(postLatencyList);
        double mean = calculateAverage(latencyList);

        System.out.println("----------------------------------------------------------");
        System.out.println();
        System.out.println("Number of successful get requests:" + successGetRequests);
        System.out.println("Number of successful post requests:" + successPostRequests);
        System.out.println("Total number of successful requests:" + (successGetRequests + successPostRequests));
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println("Get latency median: " + getMedian + " ms");
        System.out.println("Post latency median: " + postMedian + " ms");
        System.out.println("All latencies median: " + median + " ms");
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println("Get latency mean: " + getMean + " ms");
        System.out.println("Post latency mean: " + postMean + " ms");
        System.out.println("All latencies mean: " + mean + " ms");
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println("99th percentile of GET request latency: " + calculatePercentile(getLatencyList, 0.99) + " ms");
        System.out.println("99th percentile of POST request latency: " + calculatePercentile(postLatencyList, 0.99) + " ms");
        System.out.println("99th percentile of GET&POST requests latency: " + calculatePercentile(latencyList, 0.99) + " ms");
        System.out.println();
        System.out.println("95th percentile of GET request latency: " + calculatePercentile(getLatencyList, 0.95) + " ms");
        System.out.println("95th percentile of POST request latency: " + calculatePercentile(postLatencyList, 0.95) + " ms");
        System.out.println("95th percentile of GET&POST requests latency: " + calculatePercentile(latencyList, 0.95) + " ms");


    }

    public static void main(String[] args) {
        int threadCount = 100;
        int iteration = 100;
        String ip = "http://54.213.88.175";
        String port = "8080";
        String servicePath = "webservice/webapi/myresource";
        System.out.println("Connecting to: " + ip + ":" + port + "/" + servicePath);

        HttpClient client = new HttpClient(ip, port, servicePath);
        WebTarget webTarget = client.getWebTarget();

        ThreadTester threadTester = new ThreadTester();

        List<Future<Result>> results = threadTester.runThread(iteration, threadCount, webTarget);

        System.out.println("Total number of requests: " + threadCount * iteration * 2);
        threadTester.evaluate(results);
    }


}
