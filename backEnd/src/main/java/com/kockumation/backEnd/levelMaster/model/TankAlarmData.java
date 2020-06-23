package com.kockumation.backEnd.levelMaster.model;

public class TankAlarmData {

    private int tankId;
    private float level;
    private float volume;
    private float speed;
    private float meanTemp;
    private int levelAlarm;

    public int getTankId() {
        return tankId;
    }

    public void setTankId(int tankId) {
        this.tankId = tankId;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getMeanTemp() {
        return meanTemp;
    }

    public void setMeanTemp(float meanTemp) {
        this.meanTemp = meanTemp;
    }

    public int getLevelAlarm() {
        return levelAlarm;
    }

    public void setLevelAlarm(int levelAlarm) {
        this.levelAlarm = levelAlarm;
    }

    @Override
    public String toString() {
        return "TankAlarmData{" +
                "tankId=" + tankId +
                ", level=" + level +
                ", volume=" + volume +
                ", meanTemp=" + meanTemp +
                ", levelAlarm=" + levelAlarm +
                '}';
    }
}
