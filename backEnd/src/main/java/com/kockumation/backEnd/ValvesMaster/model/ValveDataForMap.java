package com.kockumation.backEnd.ValvesMaster.model;

public class ValveDataForMap {

    private int valve_id;
    private String valve_name;
    private int valve_type;
    private int subType;
    private int errorTimeout;
    private String alarm_date;
    private String time_accepted;
    private String time_retrieved;
    private boolean alarm_active;
    private String alarm_description;
    private int valve_status;
    private boolean acknowledged;
    private boolean blue_alarm;
    private String alarm_name;
    private boolean archive;
    private boolean inserted;
    private boolean updateBlue;
    private boolean updateRed;

    public ValveDataForMap() {
          setUpdateRed(true);
          setUpdateBlue(true);
    }

    public int getValve_id() {
        return valve_id;
    }

    public void setValve_id(int valve_id) {
        this.valve_id = valve_id;
    }

    public String getValve_name() {
        return valve_name;
    }

    public void setValve_name(String valve_name) {
        this.valve_name = valve_name;
    }

    public int getValve_type() {
        return valve_type;
    }

    public void setValve_type(int valve_type) {
        this.valve_type = valve_type;
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

    public String getAlarm_date() {
        return alarm_date;
    }

    public void setAlarm_date(String alarm_date) {
        this.alarm_date = alarm_date;
    }

    public String getTime_accepted() {
        return time_accepted;
    }

    public void setTime_accepted(String time_accepted) {
        this.time_accepted = time_accepted;
    }

    public String getTime_retrieved() {
        return time_retrieved;
    }

    public void setTime_retrieved(String time_retrieved) {
        this.time_retrieved = time_retrieved;
    }

    public boolean isAlarm_active() {
        return alarm_active;
    }

    public void setAlarm_active(boolean alarm_active) {
        this.alarm_active = alarm_active;
    }

    public String getAlarm_description() {
        return alarm_description;
    }

    public void setAlarm_description(String alarm_description) {
        this.alarm_description = alarm_description;
    }

    public int getValve_status() {
        return valve_status;
    }

    public void setValve_status(int valve_status) {
        this.valve_status = valve_status;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public boolean isBlue_alarm() {
        return blue_alarm;
    }

    public void setBlue_alarm(boolean blue_alarm) {
        this.blue_alarm = blue_alarm;
    }

    public String getAlarm_name() {
        return alarm_name;
    }

    public void setAlarm_name(String alarm_name) {
        this.alarm_name = alarm_name;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }

    public boolean isUpdateBlue() {
        return updateBlue;
    }

    public void setUpdateBlue(boolean updateBlue) {
        this.updateBlue = updateBlue;
    }

    public boolean isUpdateRed() {
        return updateRed;
    }

    public void setUpdateRed(boolean updateRed) {
        this.updateRed = updateRed;
    }

    @Override
    public String toString() {
        return "ValveDataForMap{" +
                "valve_id=" + valve_id +
                ", valve_name='" + valve_name + '\'' +
                ", valve_type=" + valve_type +
                ", subType=" + subType +
                ", errorTimeout=" + errorTimeout +
                ", alarm_date='" + alarm_date + '\'' +
                ", time_accepted='" + time_accepted + '\'' +
                ", time_retrieved='" + time_retrieved + '\'' +
                ", alarm_active=" + alarm_active +
                ", alarm_description='" + alarm_description + '\'' +
                ", valve_status=" + valve_status +
                ", acknowledged=" + acknowledged +
                ", blue_alarm=" + blue_alarm +
                ", alarm_name='" + alarm_name + '\'' +
                ", archive=" + archive +
                ", inserted=" + inserted +
                ", updateBlue=" + updateBlue +
                ", updateRed=" + updateRed +
                '}';
    }
}
