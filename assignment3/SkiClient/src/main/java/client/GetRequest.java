package client;

import com.xiao.Statics;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GetRequest implements Callable<Statics> {
    private WebTarget webTarget;
    private int dayNum;
    private List<Integer> list;

    public GetRequest(WebTarget webTarget, int dayNum, List<Integer> list) {
        this.webTarget = webTarget;
        this.dayNum = dayNum;
        this.list = list;
    }

    public String getData(int skierId, int dayNum) throws ClientErrorException {
        String response = webTarget
                .queryParam("skierId", skierId)
                .queryParam("dayNum", dayNum)
                .request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);

        System.out.println("The response of get request is: " + response);
        return response;
    }

    public Statics call() throws Exception {
        List<Long> getLatencies = new ArrayList<>();
        int requestCount = 0;
        for (Integer skierId : list) {
            long getStartTime = System.currentTimeMillis();
            getData(skierId, dayNum);
            requestCount++;
            long getEndTime = System.currentTimeMillis();
            long getLatency = getEndTime - getStartTime;
            getLatencies.add(getLatency);
        }

        return new Statics(getLatencies, requestCount);
    }
}

