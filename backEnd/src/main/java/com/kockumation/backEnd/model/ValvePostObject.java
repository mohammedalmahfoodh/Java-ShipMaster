package com.kockumation.backEnd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValvePostObject {

    @NotNull
    @Min(1)
    private int valve_id;

    public int getValve_id() {
        return valve_id;
    }

    public void setValve_id(int valve_id) {
        this.valve_id = valve_id;
    }
}
