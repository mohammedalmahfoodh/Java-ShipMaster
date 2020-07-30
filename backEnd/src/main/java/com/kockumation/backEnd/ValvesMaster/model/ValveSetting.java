package com.kockumation.backEnd.ValvesMaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValveSetting {
    private int id;
    private String valve_name;
    private int type;
    private int subType;
    private int errorTimeout;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValve_name() {
        return valve_name;
    }

    public void setValve_name(String valve_name) {
        this.valve_name = valve_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public int getErrorTimeout() {
        return errorTimeout;
    }

    public void setErrorTimeout(int errorTimeout) {
        this.errorTimeout = errorTimeout;
    }

    @Override
    public String toString() {
        return "ValveSetting{" +
                "id=" + id +
                ", valve_name='" + valve_name + '\'' +
                ", type=" + type +
                ", subType=" + subType +
                ", errorTimeout=" + errorTimeout +
                '}';
    }
}
