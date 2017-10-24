package com.xiao;

import bsdsass2testdata.RFIDLiftData;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HttpRequest implements Callable<Result> {
    private WebTarget webTarget;
    private List<RFIDLiftData> rfidLiftSubList;

    public HttpRequest(WebTarget webTarget, List<RFIDLiftData> rfidLiftSubList) {
        this.webTarget = webTarget;
        this.rfidLiftSubList = rfidLiftSubList;
    }

    public void postData(RFIDLiftData data) throws ClientErrorException {
        webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(data), Integer.class);
    }

    public String getData() throws ClientErrorException {
        return webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
    }

    public Result call() throws Exception {
        List<Long> postLatencies = new ArrayList<>();
        int postSuccessCount = 0;

        for (RFIDLiftData data : rfidLiftSubList) {
            long postStartTime = System.currentTimeMillis();
            postData(data);
            postSuccessCount++;
            System.out.println(postSuccessCount);
            long postEndTime = System.currentTimeMillis();

            long postLatency = postEndTime - postStartTime;
            postLatencies.add(postLatency);
        }

        return new Result(postLatencies, postSuccessCount);
    }
}
