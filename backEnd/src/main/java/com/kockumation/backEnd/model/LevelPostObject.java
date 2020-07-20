package com.kockumation.backEnd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kockumation.backEnd.levelMaster.LavelMasterManager;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LevelPostObject {

  @NotNull @Min(1)
  private int tank_id;
    @NotNull
  private float  low_alarm_limit;
    @NotNull
  private float  high_alarm_limit;
    @NotNull
    private float density;
    @NotNull
    private float temp_limit;

    public float getTemp_limit() {
        return temp_limit;
    }

    public void setTemp_limit(float temp_limit) {
        this.temp_limit = temp_limit;
    }

    public int getTank_id() {
        return tank_id;
    }

    public void setTank_id(int tank_id) {
        this.tank_id = tank_id;
    }

    public float getLow_alarm_limit() {
        return low_alarm_limit;
    }

    public void setLow_alarm_limit(float low_alarm_limit) {
        this.low_alarm_limit = low_alarm_limit;
    }

    public float getHigh_alarm_limit() {
        return high_alarm_limit;
    }

    public void setHigh_alarm_limit(float high_alarm_limit) {
        this.high_alarm_limit = high_alarm_limit;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
