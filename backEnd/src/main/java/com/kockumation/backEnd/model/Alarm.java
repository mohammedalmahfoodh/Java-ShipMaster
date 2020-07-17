package com.kockumation.backEnd.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Alarm {
    private int alarm_id;
    private String alarm_name;
    private int temp_alarm;
    private  int tank_id ;
    private  int valve_id ;
    private  int pump_id ;
    private  String alarm_date;
    private int acknowledged;
    private String alarm_description;
    private int alarm_active;
    private int level_alarm;
    private String time_accepted;
    private String time_retrieved;
    private int blue_alarm;
    private int valve_status;
    private int pump_status;
    private int archive;
    private int color;

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public int getAlarm_id() {
    return alarm_id;
  }

  public void setAlarm_id(int alarm_id) {
    this.alarm_id = alarm_id;
  }

  public String getAlarm_name() {
    return alarm_name;
  }

  public void setAlarm_name(String alarm_name) {
    this.alarm_name = alarm_name;
  }

  public int getTemp_alarm() {
    return temp_alarm;
  }

  public void setTemp_alarm(int temp_alarm) {
    this.temp_alarm = temp_alarm;
  }

  public int getTank_id() {
    return tank_id;
  }

  public void setTank_id(int tank_id) {
    this.tank_id = tank_id;
  }

  public int getValve_id() {
    return valve_id;
  }

  public void setValve_id(int valve_id) {
    this.valve_id = valve_id;
  }

  public int getPump_id() {
    return pump_id;
  }

  public void setPump_id(int pump_id) {
    this.pump_id = pump_id;
  }

  public String getAlarm_date() {
    return alarm_date;
  }

  public void setAlarm_date(String alarm_date) {
    this.alarm_date = alarm_date;
  }

  public int getAcknowledged() {
    return acknowledged;
  }

  public void setAcknowledged(int acknowledged) {
    this.acknowledged = acknowledged;
  }

  public String getAlarm_description() {
    return alarm_description;
  }

  public void setAlarm_description(String alarm_description) {
    this.alarm_description = alarm_description;
  }

  public int getAlarm_active() {
    return alarm_active;
  }

  public void setAlarm_active(int alarm_active) {
    this.alarm_active = alarm_active;
  }

  public int getLevel_alarm() {
    return level_alarm;
  }

  public void setLevel_alarm(int level_alarm) {
    this.level_alarm = level_alarm;
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

  public int getBlue_alarm() {
    return blue_alarm;
  }

  public void setBlue_alarm(int blue_alarm) {
    this.blue_alarm = blue_alarm;
  }

  public int getValve_status() {
    return valve_status;
  }

  public void setValve_status(int valve_status) {
    this.valve_status = valve_status;
  }

  public int getPump_status() {
    return pump_status;
  }

  public void setPump_status(int pump_status) {
    this.pump_status = pump_status;
  }

  public int getArchive() {
    return archive;
  }

  public void setArchive(int archive) {
    this.archive = archive;
  }
}
