package com.kockumation.backEnd.levelMaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TankSettingData {


    private int tankId;
    private String tankCode;
    private String tankName;
    private float maxLevel;
    private float maxVolume;
    private float highLevel;
    private float highHighLevel;
    private float lowLevel;
    private float lowLowLevel;
    private String density;

    public int getTankId() {
        return tankId;
    }

    public void setTankId(int tankId) {
        this.tankId = tankId;
    }

    public String getTankCode() {
        return tankCode;
    }

    public void setTankCode(String tankCode) {
        this.tankCode = tankCode;
    }

    public String getTankName() {
        return tankName;
    }

    public void setTankName(String tankName) {
        this.tankName = tankName;
    }

    public float getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(float maxLevel) {
        this.maxLevel = maxLevel;
    }

    public float getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(float maxVolume) {
        this.maxVolume = maxVolume;
    }

    public float getHighLevel() {
        return highLevel;
    }

    public void setHighLevel(float highLevel) {
        this.highLevel = highLevel;
    }

    public float getHighHighLevel() {
        return highHighLevel;
    }

    public void setHighHighLevel(float highHighLevel) {
        this.highHighLevel = highHighLevel;
    }

    public float getLowLevel() {
        return lowLevel;
    }

    public void setLowLevel(float lowLevel) {
        this.lowLevel = lowLevel;
    }

    public float getLowLowLevel() {
        return lowLowLevel;
    }

    public void setLowLowLevel(float lowLowLevel) {
        this.lowLowLevel = lowLowLevel;
    }

    public String getDensity() {
        return density;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    @Override
    public String toString() {
        return "TankSettingsData{" +
                "tankId=" + tankId +
                ", tankCode='" + tankCode + '\'' +
                ", tankName='" + tankName + '\'' +
                ", maxLevel=" + maxLevel +
                ", maxVolume=" + maxVolume +
                ", highLevel=" + highLevel +
                ", highHighLevel=" + highHighLevel +
                ", lowLevel=" + lowLevel +
                ", lowLowLevel=" + lowLowLevel +
                ", density='" + density + '\'' +
                '}';
    }
}
