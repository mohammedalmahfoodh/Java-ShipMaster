package com.kockumation.backEnd.levelMaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KslTankData {


    private String TankCode;
    private String TName ;
    private float Volume ;
    private int VOStatus ;
    private double UllageCorr ;
    private double InnageCorr ;
    private float MeanTemp ;
    private int MTStatus ;
    private float Density;
    private int DEStatus ;
    private double Pressure ;
    private int PRStatus ;

    public String getTankCode() {
        return TankCode;
    }

    public void setTankCode(String tankCode) {
        TankCode = tankCode;
    }

    public String getTName() {
        return TName;
    }

    public void setTName(String TName) {
        this.TName = TName;
    }

    public float getVolume() {
        return Volume;
    }

    public void setVolume(float volume) {
        Volume = volume;
    }

    public int getVOStatus() {
        return VOStatus;
    }

    public void setVOStatus(int VOStatus) {
        this.VOStatus = VOStatus;
    }

    public double getUllageCorr() {
        return UllageCorr;
    }

    public void setUllageCorr(double ullageCorr) {
        UllageCorr = ullageCorr;
    }

    public double getInnageCorr() {
        return InnageCorr;
    }

    public void setInnageCorr(double innageCorr) {
        InnageCorr = innageCorr;
    }

    public float getMeanTemp() {
        return MeanTemp;
    }

    public void setMeanTemp(float meanTemp) {
        MeanTemp = meanTemp;
    }

    public void setDensity(float density) {
        Density = density;
    }

    public int getMTStatus() {
        return MTStatus;
    }

    public void setMTStatus(int MTStatus) {
        this.MTStatus = MTStatus;
    }

    public float getDensity() {
        return Density;
    }



    public int getDEStatus() {
        return DEStatus;
    }

    public void setDEStatus(int DEStatus) {
        this.DEStatus = DEStatus;
    }

    public double getPressure() {
        return Pressure;
    }

    public void setPressure(double pressure) {
        Pressure = pressure;
    }

    public int getPRStatus() {
        return PRStatus;
    }

    public void setPRStatus(int PRStatus) {
        this.PRStatus = PRStatus;
    }

    @Override
    public String toString() {
        return "KslTankData{" +
                "TankCode='" + TankCode + '\'' +
                ", TName='" + TName + '\'' +
                ", Volume=" + Volume +
                ", VOStatus=" + VOStatus +
                ", UllageCorr=" + UllageCorr +
                ", InnageCorr=" + InnageCorr +
                ", MeanTemp=" + MeanTemp +
                ", MTStatus=" + MTStatus +
                ", Density=" + Density +
                ", DEStatus=" + DEStatus +
                ", Pressure=" + Pressure +
                ", PRStatus=" + PRStatus +
                '}';
    }
}
