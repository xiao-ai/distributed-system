import java.util.List;

public class Result {
    private List<Long> getLatencies;
    private List<Long> postLatencies;
    private int getSuccessCount;
    private int postSuccessCount;

    public List<Long> getGetLatencies() {
        return getLatencies;
    }

    public List<Long> getPostLatencies() {
        return postLatencies;
    }

    public int getGetSuccessCount() {
        return getSuccessCount;
    }

    public int getPostSuccessCount() {
        return postSuccessCount;
    }

    public Result(List<Long> getLatencies, List<Long> postLatencies, int getSuccess, int postSuccess) {
        this.getLatencies = getLatencies;
        this.postLatencies = postLatencies;
        this.getSuccessCount = getSuccess;
        this.postSuccessCount = postSuccess;
    }
}
