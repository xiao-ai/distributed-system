package com.xiao;

import client.GetRequest;
import client.HttpClient;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadProcessor {
    public static WebTarget getWebTarget;
    public static long wallTime;

    private static List<Future<Statics>> runThread(WebTarget webTarget, List<List<Integer>> skierIds, int threadCount) {
        List<Callable<Statics>> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < skierIds.size(); i++) {
            tasks.add(new GetRequest(webTarget, 1, 1, skierIds.get(i)));
        }

        List<Future<Statics>> result = null;

        long start = System.currentTimeMillis();
        try {
            System.out.println("Running threads...");
            result = executor.invokeAll(tasks, 2, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        if (executor.isShutdown()) {
            long end = System.currentTimeMillis();
            wallTime = (end - start) / 1000;
            System.out.println("[Walltime]:                  " + wallTime + "s");

            return result;
        } else {
            System.out.println("System time out! exceed 2 hours!");
            return null;
        }
    }

    public static void evaluate(List<Future<Statics>> statics) {
        if (statics == null) {
            System.out.println("time out!");
        } else {
            Evaluator evaluator = new Evaluator();
            Metrics metrics = evaluator.startEvaluate(statics);
            metrics.printMetrics();
            System.out.println("[Throughput]:                " + metrics.getTotalRequests() / wallTime);
        }
    }

    public static void main(String[] args) {
        // server config
        String ip = "http://34.212.199.110";
        String port = "8080";
        String getPath = "/SkiServer/webapi/myvert";

//        String ip = "http://localhost";
//        String port = "8080";
//        String getPath = "/webapi/myvert";

        int threadCount = 100;

        // generate skierIds
        List<List<Integer>> skierIds = new ArrayList<>();

        int start = 1;
        for (int i = 1; i <= 100; i++) {
            List<Integer> list = new ArrayList<>();
            for (int j = 1; j <= 400; j++) {
                list.add(start);
                start += 1;
            }
            skierIds.add(list);
        }

        HttpClient getClient = new HttpClient(ip, port, getPath);
        getWebTarget = getClient.getWebTarget();

        List<Future<Statics>> getStatics = runThread(getWebTarget, skierIds, threadCount);

        System.out.println("[Total threads]:             " + threadCount);
        evaluate(getStatics);
    }
}
