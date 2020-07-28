package com.kockumation.backEnd.utilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValveIdAndName {

    private int id;
    private String valveName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValveName() {
        return valveName;
    }

    public void setValveName(String valveName) {
        this.valveName = valveName;
    }

    @Override
    public String toString() {
        return "valveIdAndName{" +
                "id=" + id +
                ", valveName='" + valveName + '\'' +
                '}';
    }
}
