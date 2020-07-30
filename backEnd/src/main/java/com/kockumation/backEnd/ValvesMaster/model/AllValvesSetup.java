package com.kockumation.backEnd.ValvesMaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AllValvesSetup {
    private List<ValveSetting> setSmAllValvesSetupData;

    public List<ValveSetting> getSetSmAllValvesSetupData() {
        return setSmAllValvesSetupData;
    }

    public void setSetSmAllValvesSetupData(List<ValveSetting> setSmAllValvesSetupData) {
        this.setSmAllValvesSetupData = setSmAllValvesSetupData;
    }
}
