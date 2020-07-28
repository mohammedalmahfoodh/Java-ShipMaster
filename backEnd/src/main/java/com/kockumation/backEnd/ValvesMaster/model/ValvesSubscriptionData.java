package com.kockumation.backEnd.ValvesMaster.model;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValvesSubscriptionData {
    private List<ValveAlarmData> setSmValveSubscriptionData;

    public List<ValveAlarmData> getSetSmValveSubscriptionData() {
        return setSmValveSubscriptionData;
    }

    public void setSetSmValveSubscriptionData(List<ValveAlarmData> setSmValveSubscriptionData) {
        this.setSmValveSubscriptionData = setSmValveSubscriptionData;
    }
}
