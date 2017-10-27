package com.xiao;

import bsdsass2testdata.RFIDLiftData;
import client.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadProcessor {

    public static WebTarget postWebTarget;
    public static WebTarget getWebTarget;
    public static long wallTime;

    private static List<Future<Statics>> runThread(List<List<RFIDLiftData>> list, WebTarget webTarget) {
        List<Callable<Statics>> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(list.size());

        for (int i = 0; i < 10; i++) {
            List<RFIDLiftData> sublist = list.get(i);
            tasks.add(new PostRequest(webTarget, sublist));
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
            System.out.println("[Walltime]:                  " + wallTime);

            // final call
            webTarget.request(MediaType.TEXT_PLAIN)
                    .post(Entity.entity("-1,-1,-1,-1,-1", MediaType.TEXT_PLAIN))
                    .readEntity(String.class);

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
        // localhost
//        String ip = "http://localhost";
//        String port = "8080";
//        String postPath = "/webapi/load";
//        String getPath = "/webapi/myvert";

        // server config
        String ip = "http://34.212.199.110";
        String port = "8080";
        String postPath = "/SkiServer/webapi/load";
        String getPath = "/SkiServer/webapi/myvert";

        int threadCount = 100;

        // read file
        FileProcessor fileProcessor = new FileProcessor();
        List<List<RFIDLiftData>> partitionedList = fileProcessor.partition(threadCount);

        // post data
        HttpClient postClient = new HttpClient(ip, port, postPath);
        postWebTarget = postClient.getWebTarget();
        List<Future<Statics>> postStatics = runThread(partitionedList, postWebTarget);

        System.out.println("[Total threads]:             " + threadCount);
        evaluate(postStatics);

    }
}
