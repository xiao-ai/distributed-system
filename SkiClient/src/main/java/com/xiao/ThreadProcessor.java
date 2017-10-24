package com.xiao;

import bsdsass2testdata.RFIDLiftData;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class ThreadProcessor {

    public static WebTarget webTarget;

    public List<Future<Result>> runThread(List<List<RFIDLiftData>> list, WebTarget webTarget) {
        List<Callable<Result>> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(list.size());

        for (int i = 0; i < 10; i++) {
            List<RFIDLiftData> sublist = list.get(i);
            tasks.add(new HttpRequest(webTarget, sublist));
        }
//        for (List<RFIDLiftData> sublist : list) {
//            tasks.add(new HttpRequest(webTarget, sublist));
//        }

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
            executor.awaitTermination(5, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished, total run time: " + (end - start) / 1000 + " seconds");

        return result;

    }

    public static void main(String[] args) {
        int threadCount = 100;

        // localhost
//        String ip = "http://localhost";
//        String port = "8080";
//        String postPath = "/webapi/load";
//        String getPath = "/webapi/myvert";

        // server
        String ip = "http://34.212.199.110";
        String port = "8080";
        String postPath = "/SkiServer/webapi/load";
        String getPath = "/SkiServer/webapi/myvert";

        System.out.println("Connecting to: " + ip + ":" + port + postPath);
        HttpClient client = new HttpClient(ip, port, postPath);
        webTarget = client.getWebTarget();

        // read file
        FileProcessor fileProcessor = new FileProcessor();
        List<List<RFIDLiftData>> partitionedList = fileProcessor.partition(fileProcessor.readFile(), threadCount);

        ThreadProcessor threadProcessor = new ThreadProcessor();

        List<Future<Result>> results = threadProcessor.runThread(partitionedList, webTarget);

        Evaluator evaluator = new Evaluator();
        evaluator.startEvaluate(results);
    }
}
