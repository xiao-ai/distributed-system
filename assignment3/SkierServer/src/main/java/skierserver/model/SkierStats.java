package skierserver.model;

public class SkierStats {
    private int skierId;
    private int dayNum;
    private int vertical;

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

    public int getVertical() {
        return vertical;
    }

    public void setVertical(int vertical) {
        this.vertical = vertical;
    }

    public SkierStats(int skierId, int dayNum, int verticalSum) {
        this.skierId = skierId;
        this.dayNum = dayNum;
        this.vertical = verticalSum;
    }

}
