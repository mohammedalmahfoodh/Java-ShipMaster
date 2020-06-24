package com.kockumation.backEnd.levelMaster.model;

public class TankDataForMap {

    private float volume;
    private int tank_id;
    private float meanTemp;
    private int temperature_limit;
    private boolean update_Temperature_alarm;
    private boolean update_Temperature_blue_alarm;
    private boolean update_Temperature_archive;
    private boolean temp_inserted;
    private boolean temp_acknowledged;
    private boolean temp_alarm_active;
    private boolean temp_blue_alarm;
    private boolean temp_archive;
    private String temp_alarm_name;
    private String temp_alarm_description;
    private String tepm_alarm_date;
    private String tepm_time_accepted;
    private float density;
    private String code_name;
    private boolean archive;
    private boolean updateH;
    private boolean updateHH;
    private boolean updateL;
    private boolean updateLL;
    private boolean updateBlue;
    private boolean inserted;
    private boolean acknowledged;
    private boolean alarm_active;
    private int level_alarm;
    private boolean blue_alarm;
    private String alarm_name;
    private String alarm_description;
    private String alarm_date;
    private String time_accepted;
    private String time_retrieved;
    private float  max_volume;
    private float tankHighLevel;
    private float tankLowLevel;
    private float tankLowLowLevel;
    private float highHighLevel;

    public TankDataForMap() {
        meanTemp =0;
        temperature_limit = 90;
        update_Temperature_alarm = true;
        update_Temperature_blue_alarm = true;
        update_Temperature_archive = true;
        temp_inserted = false;
        temp_acknowledged = false;
        temp_alarm_active = false;
        temp_blue_alarm = false;
        temp_archive = false;
        archive = false;
        updateH = true;
        updateHH = true;
        updateL = true;
        updateLL = true;
        updateBlue = true;
        inserted = false;
        acknowledged = false;
        alarm_active = false;
        blue_alarm =false;

    }

    public float getMax_volume() {
        return max_volume;
    }

    public void setMax_volume(float max_volume) {
        this.max_volume = max_volume;
    }

    public float getTankHighLevel() {
        return tankHighLevel;
    }

    public void setTankHighLevel(float tankHighLevel) {
        this.tankHighLevel = tankHighLevel;
    }

    public float getTankLowLevel() {
        return tankLowLevel;
    }

    public void setTankLowLevel(float tankLowLevel) {
        this.tankLowLevel = tankLowLevel;
    }

    public float getTankLowLowLevel() {
        return tankLowLowLevel;
    }

    public void setTankLowLowLevel(float tankLowLowLevel) {
        this.tankLowLowLevel = tankLowLowLevel;
    }

    public float getHighHighLevel() {
        return highHighLevel;
    }

    public void setHighHighLevel(float highHighLevel) {
        this.highHighLevel = highHighLevel;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public int getTank_id() {
        return tank_id;
    }

    public void setTank_id(int tank_id) {
        this.tank_id = tank_id;
    }

    public float getMeanTemp() {
        return meanTemp;
    }

    public void setMeanTemp(float meanTemp) {
        this.meanTemp = meanTemp;
    }

    public int getTemperature_limit() {
        return temperature_limit;
    }

    public void setTemperature_limit(int temperature_limit) {
        this.temperature_limit = temperature_limit;
    }

    public boolean isUpdate_Temperature_alarm() {
        return update_Temperature_alarm;
    }

    public void setUpdate_Temperature_alarm(boolean update_Temperature_alarm) {
        this.update_Temperature_alarm = update_Temperature_alarm;
    }

    public boolean isUpdate_Temperature_blue_alarm() {
        return update_Temperature_blue_alarm;
    }

    public void setUpdate_Temperature_blue_alarm(boolean update_Temperature_blue_alarm) {
        this.update_Temperature_blue_alarm = update_Temperature_blue_alarm;
    }

    public boolean isUpdate_Temperature_archive() {
        return update_Temperature_archive;
    }

    public void setUpdate_Temperature_archive(boolean update_Temperature_archive) {
        this.update_Temperature_archive = update_Temperature_archive;
    }

    public boolean isTemp_inserted() {
        return temp_inserted;
    }

    public void setTemp_inserted(boolean temp_inserted) {
        this.temp_inserted = temp_inserted;
    }

    public boolean isTemp_acknowledged() {
        return temp_acknowledged;
    }

    public void setTemp_acknowledged(boolean temp_acknowledged) {
        this.temp_acknowledged = temp_acknowledged;
    }

    public boolean isTemp_alarm_active() {
        return temp_alarm_active;
    }

    public void setTemp_alarm_active(boolean temp_alarm_active) {
        this.temp_alarm_active = temp_alarm_active;
    }

    public boolean isTemp_blue_alarm() {
        return temp_blue_alarm;
    }

    public void setTemp_blue_alarm(boolean temp_blue_alarm) {
        this.temp_blue_alarm = temp_blue_alarm;
    }

    public boolean isTemp_archive() {
        return temp_archive;
    }

    public void setTemp_archive(boolean temp_archive) {
        this.temp_archive = temp_archive;
    }

    public String getTemp_alarm_name() {
        return temp_alarm_name;
    }

    public void setTemp_alarm_name(String temp_alarm_name) {
        this.temp_alarm_name = temp_alarm_name;
    }

    public String getTemp_alarm_description() {
        return temp_alarm_description;
    }

    public void setTemp_alarm_description(String temp_alarm_description) {
        this.temp_alarm_description = temp_alarm_description;
    }

    public String getTepm_alarm_date() {
        return tepm_alarm_date;
    }

    public void setTepm_alarm_date(String tepm_alarm_date) {
        this.tepm_alarm_date = tepm_alarm_date;
    }

    public String getTepm_time_accepted() {
        return tepm_time_accepted;
    }

    public void setTepm_time_accepted(String tepm_time_accepted) {
        this.tepm_time_accepted = tepm_time_accepted;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public String getCode_name() {
        return code_name;
    }

    public void setCode_name(String code_name) {
        this.code_name = code_name;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public boolean isUpdateH() {
        return updateH;
    }

    public void setUpdateH(boolean updateH) {
        this.updateH = updateH;
    }

    public boolean isUpdateHH() {
        return updateHH;
    }

    public void setUpdateHH(boolean updateHH) {
        this.updateHH = updateHH;
    }

    public boolean isUpdateL() {
        return updateL;
    }

    public void setUpdateL(boolean updateL) {
        this.updateL = updateL;
    }

    public boolean isUpdateLL() {
        return updateLL;
    }

    public void setUpdateLL(boolean updateLL) {
        this.updateLL = updateLL;
    }

    public boolean isUpdateBlue() {
        return updateBlue;
    }

    public void setUpdateBlue(boolean updateBlue) {
        this.updateBlue = updateBlue;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public boolean isAlarm_active() {
        return alarm_active;
    }

    public void setAlarm_active(boolean alarm_active) {
        this.alarm_active = alarm_active;
    }

    public int getLevel_alarm() {
        return level_alarm;
    }

    public void setLevel_alarm(int level_alarm) {
        this.level_alarm = level_alarm;
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

    public String getAlarm_description() {
        return alarm_description;
    }

    public void setAlarm_description(String alarm_description) {
        this.alarm_description = alarm_description;
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

    @Override
    public String toString() {
        return "TankDataForMap{" +
                "volume=" + volume +
                ", tank_id=" + tank_id +
                ", meanTemp=" + meanTemp +
                ", temperature_limit=" + temperature_limit +
                ", update_Temperature_alarm=" + update_Temperature_alarm +
                ", update_Temperature_blue_alarm=" + update_Temperature_blue_alarm +
                ", update_Temperature_archive=" + update_Temperature_archive +
                ", temp_inserted=" + temp_inserted +
                ", temp_acknowledged=" + temp_acknowledged +
                ", temp_alarm_active=" + temp_alarm_active +
                ", temp_blue_alarm=" + temp_blue_alarm +
                ", temp_archive=" + temp_archive +
                ", temp_alarm_name='" + temp_alarm_name + '\'' +
                ", temp_alarm_description='" + temp_alarm_description + '\'' +
                ", tepm_alarm_date='" + tepm_alarm_date + '\'' +
                ", tepm_time_accepted='" + tepm_time_accepted + '\'' +
                ", density=" + density +
                ", code_name='" + code_name + '\'' +
                ", archive=" + archive +
                ", updateH=" + updateH +
                ", updateHH=" + updateHH +
                ", updateL=" + updateL +
                ", updateLL=" + updateLL +
                ", updateBlue=" + updateBlue +
                ", inserted=" + inserted +
                ", acknowledged=" + acknowledged +
                ", alarm_active=" + alarm_active +
                ", level_alarm=" + level_alarm +
                ", blue_alarm=" + blue_alarm +
                ", alarm_name='" + alarm_name + '\'' +
                ", alarm_description='" + alarm_description + '\'' +
                ", alarm_date='" + alarm_date + '\'' +
                ", time_accepted='" + time_accepted + '\'' +
                ", time_retrieved='" + time_retrieved + '\'' +
                ", max_volume=" + max_volume +
                ", tankHighLevel=" + tankHighLevel +
                ", tankLowLevel=" + tankLowLevel +
                ", tankLowLowLevel=" + tankLowLowLevel +
                ", highHighLevel=" + highHighLevel +
                '}';
    }
}
