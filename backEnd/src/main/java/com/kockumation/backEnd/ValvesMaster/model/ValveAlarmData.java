package com.kockumation.backEnd.ValvesMaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValveAlarmData {

    private int id;
    private float actualPos;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getActualPos() {
        return actualPos;
    }

    public void setActualPos(float actualPos) {
        this.actualPos = actualPos;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ValveAlarmData{" +
                "id=" + id +
                ", actualPos=" + actualPos +
                ", status=" + status +
                '}';
    }
}
