package com.xiao;

public class RFIDModel {
    private int id;
    private int resortID;
    private int dayNum;
    private int skierID;
    private int liftID;
    private int time;
    private int vertical;

    public RFIDModel(int id, int resortID, int dayNum, int skierID, int liftID, int time, int vertical) {
        this.id = id;
        this.resortID = resortID;
        this.dayNum = dayNum;
        this.skierID = skierID;
        this.liftID = liftID;
        this.time = time;
        this.vertical = vertical;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResortID() {
        return resortID;
    }

    public void setResortID(int resortID) {
        this.resortID = resortID;
    }

    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    public int getSkierID() {
        return skierID;
    }

    public void setSkierID(int skierID) {
        this.skierID = skierID;
    }

    public int getLiftID() {
        return liftID;
    }

    public void setLiftID(int liftID) {
        this.liftID = liftID;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getVertical() {
        return vertical;
    }

    public void setVertical(int vertical) {
        this.vertical = vertical;
    }

    @Override
    public String toString() {
        return "RFIDModel{" +
                "id=" + id +
                ", resortID=" + resortID +
                ", dayNum=" + dayNum +
                ", skierID=" + skierID +
                ", liftID=" + liftID +
                ", time=" + time +
                ", vertical=" + vertical +
                '}';
    }
}
