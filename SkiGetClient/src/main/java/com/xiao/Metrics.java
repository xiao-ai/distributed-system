package com.xiao;

import java.util.List;

public class Metrics {
    private int totalRequests;
    private double meanLatency;
    private double medianLatency;
    private long nightyNinePercentile;
    private long nightyFivePercentile;
    private List<Long> latencyList;

    public Metrics(int totalRequests, double meanLatency, double medianLatency, long nightyNinePercentile, long nightyFivePercentile, List<Long> latencyList) {
        this.totalRequests = totalRequests;
        this.meanLatency = meanLatency;
        this.medianLatency = medianLatency;
        this.nightyNinePercentile = nightyNinePercentile;
        this.nightyFivePercentile = nightyFivePercentile;
        this.latencyList = latencyList;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public double getMeanLatency() {
        return meanLatency;
    }

    public void setMeanLatency(double meanLatency) {
        this.meanLatency = meanLatency;
    }

    public double getMedianLatency() {
        return medianLatency;
    }

    public void setMedianLatency(double medianLatency) {
        this.medianLatency = medianLatency;
    }

    public long getNightyNinePercentile() {
        return nightyNinePercentile;
    }

    public void setNightyNinePercentile(long nightyNinePercentile) {
        this.nightyNinePercentile = nightyNinePercentile;
    }

    public long getNightyFivePercentile() {
        return nightyFivePercentile;
    }

    public void setNightyFivePercentile(long nightyFivePercentile) {
        this.nightyFivePercentile = nightyFivePercentile;
    }

    public List<Long> getLatencyList() {
        return latencyList;
    }

    public void setLatencyList(List<Long> latencyList) {
        this.latencyList = latencyList;
    }

    public void printMetrics() {
        System.out.println("[Total requests]:            " + totalRequests);
        System.out.println("[Latency - Median]:          " + meanLatency + " ms");
        System.out.println("[Latency - Mean]:            " + medianLatency + " ms");
        System.out.println("[Latency - 99th Percentile]: " + nightyNinePercentile + " ms");
        System.out.println("[Latency - 95th Percentile]: " + nightyFivePercentile + " ms");
    }
}
