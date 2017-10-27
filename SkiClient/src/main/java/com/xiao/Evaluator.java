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

    public Metrics startEvaluate(List<Future<Statics>> results) {
        int totalRequests = 0;
        List<Long> latencyList = new ArrayList<>();

        for (Future<Statics> future : results) {
            try {
                Statics statics = future.get();
                totalRequests += statics.getRequestCount();
                latencyList.addAll(statics.getLatencies());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        Collections.sort(latencyList);

        double medianLatency = getMedian(latencyList);

        double meanLatency = getMean(latencyList);

        long nightyNinePercentile = getPercentile(latencyList, 0.99);

        long nightyFivePerventile = getPercentile(latencyList, 0.95);

        return new Metrics(totalRequests, meanLatency, medianLatency, nightyNinePercentile, nightyFivePerventile, latencyList);
    }
}
