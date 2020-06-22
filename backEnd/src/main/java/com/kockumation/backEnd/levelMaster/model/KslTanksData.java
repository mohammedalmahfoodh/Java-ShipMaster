package com.kockumation.backEnd.levelMaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class KslTanksData {
   private List<KslTankData> setKslTankData;

    public List<KslTankData> getSetKslTankData() {
        return setKslTankData;
    }

    public void setSetKslTankData(List<KslTankData> setKslTankData) {
        this.setKslTankData = setKslTankData;
    }

    @Override
    public String toString() {
        return "KslTanksData{" +
                "setKslTankData=" + setKslTankData +
                '}';
    }
}
