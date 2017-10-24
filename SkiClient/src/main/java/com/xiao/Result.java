package com.xiao;

import java.util.List;

public class Result {
    private List<Long> postLatencies;
    private int postSuccessCount;

    public List<Long> getPostLatencies() {
        return postLatencies;
    }

    public int getPostSuccessCount() {
        return postSuccessCount;
    }

    public Result(List<Long> postLatencies, int postSuccessCount) {
        this.postLatencies = postLatencies;
        this.postSuccessCount = postSuccessCount;
    }
}
