import java.util.List;

public class Result {
    private List<Long> getLatencies;
    private List<Long> postLatencies;
    private int getSuccess;
    private int postSuccess;

    public List<Long> getGetLatencies() {
        return getLatencies;
    }

    public List<Long> getPostLatencies() {
        return postLatencies;
    }

    public int getGetSuccess() {
        return getSuccess;
    }

    public int getPostSuccess() {
        return postSuccess;
    }

    public Result(List<Long> getLatencies, List<Long> postLatencies, int getSuccess, int postSuccess) {
        this.getLatencies = getLatencies;
        this.postLatencies = postLatencies;
        this.getSuccess = getSuccess;
        this.postSuccess = postSuccess;
    }
}
