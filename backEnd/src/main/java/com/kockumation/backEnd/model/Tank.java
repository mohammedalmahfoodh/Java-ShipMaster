package com.kockumation.backEnd.model;

public class Tank {
    private int tank_id;
    private String code_name;
    private String alarm_name;
    private String temp_alarm_name;
    private float tank_level;
    private float tank_temperature;
    private float volume;
    private float max_volume;
    private float volume_percent;
    private float weight;
    private float density;
    private float low_alarm_limit;
    private float low_low_alarm_limit;
    private float high_alarm_limit;
    private float high_high_alarm_limit;

    public int getTank_id() {
        return tank_id;
    }

    public void setTank_id(int tank_id) {
        this.tank_id = tank_id;
    }

    public String getCode_name() {
        return code_name;
    }

    public void setCode_name(String code_name) {
        this.code_name = code_name;
    }

    public String getAlarm_name() {
        return alarm_name;
    }

    public void setAlarm_name(String alarm_name) {
        this.alarm_name = alarm_name;
    }

    public String getTemp_alarm_name() {
        return temp_alarm_name;
    }

    public void setTemp_alarm_name(String temp_alarm_name) {
        this.temp_alarm_name = temp_alarm_name;
    }

    public float getTank_level() {
        return tank_level;
    }

    public void setTank_level(float tank_level) {
        this.tank_level = tank_level;
    }

    public float getTank_temperature() {
        return tank_temperature;
    }

    public void setTank_temperature(float tank_temperature) {
        this.tank_temperature = tank_temperature;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getMax_volume() {
        return max_volume;
    }

    public void setMax_volume(float max_volume) {
        this.max_volume = max_volume;
    }

    public float getVolume_percent() {
        return volume_percent;
    }

    public void setVolume_percent(float volume_percent) {
        this.volume_percent = volume_percent;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getLow_alarm_limit() {
        return low_alarm_limit;
    }

    public void setLow_alarm_limit(float low_alarm_limit) {
        this.low_alarm_limit = low_alarm_limit;
    }

    public float getLow_low_alarm_limit() {
        return low_low_alarm_limit;
    }

    public void setLow_low_alarm_limit(float low_low_alarm_limit) {
        this.low_low_alarm_limit = low_low_alarm_limit;
    }

    public float getHigh_alarm_limit() {
        return high_alarm_limit;
    }

    public void setHigh_alarm_limit(float high_alarm_limit) {
        this.high_alarm_limit = high_alarm_limit;
    }

    public float getHigh_high_alarm_limit() {
        return high_high_alarm_limit;
    }

    public void setHigh_high_alarm_limit(float high_high_alarm_limit) {
        this.high_high_alarm_limit = high_high_alarm_limit;
    }
}
