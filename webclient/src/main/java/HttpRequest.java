import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.WebTarget;
import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;

public class HttpRequest implements Callable<Result> {
    private int iteration;
    private WebTarget webTarget;

    private int getSuccess = 0;
    private int postSuccess = 0;

    public HttpRequest(int iteration, WebTarget webTarget) {
        this.iteration = iteration;
        this.webTarget = webTarget;
    }

    public int postText(Object requestEntity) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.TEXT_PLAIN), Integer.class);
    }

    public String getIt() throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public Result call() throws Exception {
        List<Long> getLatencies = new ArrayList<>();
        List<Long> postLatencies = new ArrayList<>();

        for (int i = 0; i < iteration; i++) {
            long getStartTime = System.currentTimeMillis();
            if (getIt().equals("alive")) {
                getSuccess++;
            }

            long getEndTime = System.currentTimeMillis();

            if (postText("test") == 4) {
                postSuccess++;
            }

            long postEndTime = System.currentTimeMillis();


            long getLatency = getEndTime - getStartTime;
            System.out.println(getLatency);
            long postLatency = postEndTime - getEndTime;

            getLatencies.add(getLatency);
            postLatencies.add(postLatency);
        }

        return new Result(getLatencies, postLatencies, getSuccess, postSuccess);
    }
}


