package skiserver.model;

public class SkierStats {
    private int skierId;
    private int dayNum;
    private int verticalSum;
    private int count;

    public int getSkierId() {
        return skierId;
    }

    public void setSkierId(int skierId) {
        this.skierId = skierId;
    }

    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    public int getVerticalSum() {
        return verticalSum;
    }

    public void setVerticalSum(int verticalSum) {
        this.verticalSum = verticalSum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public SkierStats(int skierId, int dayNum, int verticalSum, int count) {
        this.skierId = skierId;
        this.dayNum = dayNum;
        this.verticalSum = verticalSum;
        this.count = count;
    }

    @Override
    public String toString() {
        return "SkierStats{" +
                "skierId=" + skierId +
                ", dayNum=" + dayNum +
                ", verticalSum=" + verticalSum +
                ", count=" + count +
                '}';
    }
}
