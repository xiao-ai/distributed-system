package client;

import bsdsass2testdata.RFIDLiftData;
import com.xiao.Statics;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PostRequest implements Callable<Statics> {
    private WebTarget webTarget;
    private List<RFIDLiftData> rfidLiftSubList;

    public PostRequest(WebTarget webTarget, List<RFIDLiftData> rfidLiftSubList) {
        this.webTarget = webTarget;
        this.rfidLiftSubList = rfidLiftSubList;
    }

    public void postData(String request) throws ClientErrorException {
        String response = webTarget.request(MediaType.TEXT_PLAIN).post(Entity.entity(request, MediaType.TEXT_PLAIN)).readEntity(String.class);
        System.out.println("The response of post request is: " + response);
    }

    public Statics call() throws Exception {
        List<Long> postLatencies = new ArrayList<>();
        int postSuccessCount = 0;

        for (RFIDLiftData data : rfidLiftSubList) {
            long postStartTime = System.currentTimeMillis();
            postData(data.toString());
            postSuccessCount++;
            long postEndTime = System.currentTimeMillis();

            long postLatency = postEndTime - postStartTime;
            postLatencies.add(postLatency);
        }

        return new Statics(postLatencies, postSuccessCount);
    }
}
