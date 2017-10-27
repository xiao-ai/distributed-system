package com.xiao;

import java.util.List;

public class Statics {
    private List<Long> latencies;
    private int requestCount;

    public List<Long> getLatencies() {
        return latencies;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public Statics(List<Long> latencies, int requestCount) {
        this.latencies = latencies;
        this.requestCount = requestCount;
    }
}
