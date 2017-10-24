package com.xiao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Evaluator {

    public long getMedian(List<Long> list) {
        int middle = list.size() / 2;
        if (list.size() % 2 == 1) {
            return list.get(middle);
        } else {
            return (list.get(middle - 1) + list.get(middle) / 2);
        }
    }

    public double getMean(List<Long> list) {
        int sum = 0;
        for (long l : list)
            sum += l;
        double mean = 1.0d * sum / list.size();

        return mean;
    }

    public long getPercentile(List<Long> list, double percentile) {
        double index = Math.round(list.size() * percentile);
        return list.get((int) index);
    }

    public void startEvaluate(List<Future<Result>> results) {
        int successPostRequests = 0;
        List<Long> postLatencyList = new ArrayList<>();

        for (Future<Result> future : results) {
            try {
                Result result = future.get();
                successPostRequests += result.getPostSuccessCount();
                postLatencyList.addAll(result.getPostLatencies());

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        Collections.sort(postLatencyList);

        double postMedian = getMedian(postLatencyList);

        double postMean = getMean(postLatencyList);

        System.out.println("[Successful requests]:       " + successPostRequests);
        System.out.println("[Latency - Median]:          " + postMedian + " ms");
        System.out.println("[Latency - Mean]:            " + postMean + " ms");
        System.out.println("[Latency - 99th Percentile]: " + getPercentile(postLatencyList, 0.99) + " ms");
        System.out.println("[Latency - 95th Percentile]: " + getPercentile(postLatencyList, 0.95) + " ms");

    }
}
