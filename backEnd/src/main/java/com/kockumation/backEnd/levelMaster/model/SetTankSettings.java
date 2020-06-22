package com.kockumation.backEnd.levelMaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kockumation.backEnd.levelMaster.TankSettingsData;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetTankSettings {
  TankSettingsData setTankSettingsData= new TankSettingsData();

    public TankSettingsData getSetTankSettingsData() {
        return setTankSettingsData;
    }

    public void setSetTankSettingsData(TankSettingsData setTankSettingsData) {
        this.setTankSettingsData = setTankSettingsData;
    }
}
