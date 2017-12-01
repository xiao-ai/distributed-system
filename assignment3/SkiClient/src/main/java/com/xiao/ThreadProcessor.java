package com.xiao;

import bsdsass2testdata.RFIDLiftData;
import client.*;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadProcessor {

    public static WebTarget postWebTarget;
    public static WebTarget getWebTarget;
    public static long wallTime;

    private static List<List<Integer>> generateSkierIds(int threadCount) {
        List<List<Integer>> skierIds = new ArrayList<>();

        int start = 1;
        for (int i = 1; i <= threadCount; i++) {
            List<Integer> list = new ArrayList<>();
            for (int j = 1; j <= 30; j++) {
                list.add(start);
                start += 1;
            }
            skierIds.add(list);
        }

        return skierIds;
    }

    private static List<Future<Statics>> runGetThread(WebTarget webTarget, List<List<Integer>> skierIds, int dayNum) {
        List<Callable<Statics>> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(skierIds.size());

        for (int i = 0; i < skierIds.size(); i++) {
            tasks.add(new GetRequest(webTarget, dayNum, skierIds.get(i)));
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

    private static List<Future<Statics>> runPostThread(List<List<RFIDLiftData>> list, WebTarget webTarget) {
        List<Callable<Statics>> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(list.size());

        for (int i = 0; i < list.size(); i++) {
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
        String ip = "http://dbms-load-balancer-751169865.us-west-2.elb.amazonaws.com";
        String port = "8080";
        String postPath = "/SkierServer/webapi/load";
        String getPath = "/SkierServer/webapi/myvert";

        int threadCount = 100;

        // read file
        FileProcessor fileProcessor = new FileProcessor();
        List<List<RFIDLiftData>> partitionedList = fileProcessor.partition(threadCount);

        // post data
        HttpClient postClient = new HttpClient(ip, port, postPath);
        postWebTarget = postClient.getWebTarget();
        List<Future<Statics>> postStatics = runPostThread(partitionedList, postWebTarget);

        System.out.println("[Total threads]:             " + threadCount);
        evaluate(postStatics);

        // get data
//        HttpClient getClient = new HttpClient(ip, port, getPath);
//        getWebTarget = getClient.getWebTarget();
//
//        List<List<Integer>> skierIds = generateSkierIds(threadCount);
//
//        List<Future<Statics>> getStatics = runGetThread(getWebTarget, skierIds, 999);
//
//        System.out.println("[Total threads]:             " + threadCount);
//        evaluate(getStatics);

    }
}
