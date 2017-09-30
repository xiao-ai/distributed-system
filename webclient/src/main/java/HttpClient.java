import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class HttpClient {
    private WebTarget webTarget;
    private Client client;

    public HttpClient (String ip, String port, String servicePath) {
        String url = ip + ":" + port;
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(url).path(servicePath);
    }

    public WebTarget getWebTarget() {
        return webTarget;
    }
}
