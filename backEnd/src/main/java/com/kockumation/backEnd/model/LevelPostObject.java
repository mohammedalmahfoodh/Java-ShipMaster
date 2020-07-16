package com.kockumation.backEnd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LevelPostObject {
   private int tank_id;

    public int getTank_id() {
        return tank_id;
    }

    public void setTank_id(int tank_id) {
        this.tank_id = tank_id;
    }
}
