package com.kockumation.backEnd.levelMaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kockumation.backEnd.levelMaster.TankSettingsData;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetTankSettings {
    TankSettingData setTankSettingsData= new TankSettingData();

    public SetTankSettings() {

    }

    public TankSettingData getSetTankSettingsData() {
        return setTankSettingsData;
    }

    public void setSetTankSettingsData(TankSettingData setTankSettingsData) {
        this.setTankSettingsData = setTankSettingsData;
    }
}
