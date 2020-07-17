package com.kockumation.backEnd.levelMaster;

import com.kockumation.backEnd.levelMaster.model.TankAlarmData;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class DetectAndSaveAlarms extends Thread {
    public static Timer timer;
    public static boolean firstRun = true;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();

    public DetectAndSaveAlarms() {
        timer = new Timer();
    }

    // Run function ****************  Run function ******************
    public void run() {
        //  System.out.println("Inside DetectAndSave Alarms " + Thread.currentThread());
        while (!Thread.currentThread().isInterrupted() && firstRun) {

            boolean makeAlarmsArchived = false;
            try {
                makeAlarmsArchived = makeUnresolvedAlarmsArchived().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (makeAlarmsArchived) {

                System.out.println("Unresolved Alarms becomes Archived ");
                firstRun = false;
                detectAlarms();
            } else {
                System.out.println("Error connecting database connect to db to update alarms");
            }

        } // while (!Thread.currentThread().isInterrupted() && firstRun)
    }// Run function ****************  Run function ******************

    // Make Unresolved Alarms Archived  ****************************** Make Unresolved Alarms Archived    ***************************************
    public Future<Boolean> makeUnresolvedAlarmsArchived() {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            String updateAlarms = "UPDATE alarms set archive= ?,alarm_description = ?,blue_alarm = 0,alarm_active = 0,temp_alarm = 0 where alarm_active = 1 || blue_alarm = 1;";
            PreparedStatement preparedStmt = conn.prepareStatement(updateAlarms, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setInt(1, 1);
            preparedStmt.setString(2, "Archived Alarm");

            int rowAffected = preparedStmt.executeUpdate();
            System.out.println(rowAffected + " Alarms updated");
            String updateTanks = "UPDATE tanks set alarm_name = null ;";
            PreparedStatement preparedStmt2 = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
            int rowAffected2 = preparedStmt2.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {
            //  Thread.sleep(3000);
            return true;
        });

    }// Make Unresolved Alarms Archived  ****************************** Make Unresolved Alarms Archived    ***************************************


    // Insert new alarm into alarms table ****************************** Insert new alarm into alarms table   ***************************************
    public Future<Boolean> insertNewLevelAlarm(TankDataForMap tankDataForMap) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String query = "INSERT INTO alarms (alarm_name,tank_id,acknowledged,alarm_description,level_alarm,archive,alarm_date,time_accepted,alarm_active,blue_alarm,time_retrieved) VALUES (?,?,?,?,?,?,?,?,?,?,?);";

            PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, tankDataForMap.getAlarm_name());
            preparedStmt.setInt(2, tankDataForMap.getTank_id());
            preparedStmt.setBoolean(3, tankDataForMap.isAcknowledged());
            preparedStmt.setString(4, tankDataForMap.getAlarm_description());
            preparedStmt.setInt(5, tankDataForMap.getLevel_alarm());
            preparedStmt.setBoolean(6, tankDataForMap.isArchive());
            preparedStmt.setString(7, tankDataForMap.getAlarm_date());
            preparedStmt.setString(8, tankDataForMap.getTime_accepted());
            preparedStmt.setBoolean(9, tankDataForMap.isAlarm_active());
            preparedStmt.setBoolean(10, tankDataForMap.isBlue_alarm());
            preparedStmt.setString(11, tankDataForMap.getTime_retrieved());
            int rowAffected = preparedStmt.executeUpdate();
            // System.out.println(rowAffected);
            String updateTanks = "UPDATE tanks set alarm_name = ? where tank_id = ?;";
            PreparedStatement preparedStmt2 = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
            preparedStmt2.setString(1, tankDataForMap.getAlarm_name());
            preparedStmt2.setInt(2, tankDataForMap.getTank_id());
            int rowAffected2 = preparedStmt2.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {


            //  Thread.sleep(3000);
            return true;
        });

    }// Insert new alarm into alarms table ****************************** Insert new alarm into alarms table   ***************************************

    // Update level alarm  ****************************** Update level alarm   ***************************************
    public Future<Boolean> updateLevelAlarm(TankDataForMap tankDataForMap) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            //  String query = "INSERT INTO alarms (alarm_name,tank_id,acknowledged,alarm_description,level_alarm,archive,alarm_date,time_accepted,alarm_active,blue_alarm,time_retrieved) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
            String updateAlarms = "UPDATE alarms set alarm_name= ?,alarm_description = ?,blue_alarm = ?,alarm_date =? ,time_retrieved =?,alarm_active =?,time_accepted =?,acknowledged=?,level_alarm = ? where tank_id = ? && (temp_alarm = 0) && (archive = 0);";
            PreparedStatement preparedStmt = conn.prepareStatement(updateAlarms, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, tankDataForMap.getAlarm_name());
            preparedStmt.setString(2, tankDataForMap.getAlarm_description());
            preparedStmt.setBoolean(3, tankDataForMap.isBlue_alarm());
            preparedStmt.setString(4, tankDataForMap.getAlarm_date());
            preparedStmt.setString(5, tankDataForMap.getTime_retrieved());
            preparedStmt.setBoolean(6, tankDataForMap.isAlarm_active());
            preparedStmt.setString(7, tankDataForMap.getTime_accepted());
            preparedStmt.setBoolean(8, tankDataForMap.isAcknowledged());
            preparedStmt.setInt(9, tankDataForMap.getLevel_alarm());
            preparedStmt.setInt(10, tankDataForMap.getTank_id());
            int rowAffected = preparedStmt.executeUpdate();
            //  System.out.println(rowAffected);
            String updateTanks = "UPDATE tanks set alarm_name = ? where tank_id = ?;";
            PreparedStatement preparedStmt2 = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
            preparedStmt2.setString(1, tankDataForMap.getAlarm_name());
            preparedStmt2.setInt(2, tankDataForMap.getTank_id());
            int rowAffected2 = preparedStmt2.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {
            //  Thread.sleep(3000);
            return true;
        });

    }// Update level alarm  ****************************** Update level alarm   ***************************************

    // Update Archive alarms  ****************************** Update Archive alarms   ***************************************
    public Future<Boolean> updateArchivedAlarm(TankDataForMap tankDataForMap) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            String updateAlarms = "UPDATE alarms set alarm_description= ?,blue_alarm = ?,time_retrieved = ?,alarm_active =? ,time_accepted =?,archive =? where tank_id = ? && (alarm_active = 1 || blue_alarm = 1) && (temp_alarm = 0);";
            PreparedStatement preparedStmt = conn.prepareStatement(updateAlarms, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, tankDataForMap.getAlarm_description());
            preparedStmt.setBoolean(2, tankDataForMap.isBlue_alarm());
            preparedStmt.setString(3, tankDataForMap.getTime_retrieved());
            preparedStmt.setBoolean(4, tankDataForMap.isAlarm_active());
            preparedStmt.setString(5, tankDataForMap.getTime_accepted());
            preparedStmt.setBoolean(6, tankDataForMap.isArchive());
            preparedStmt.setInt(7, tankDataForMap.getTank_id());
            int rowAffected = preparedStmt.executeUpdate();
            // System.out.println(rowAffected);

            String updateTanks = "UPDATE tanks set alarm_name = null where tank_id = ?;";
            PreparedStatement preparedStmt2 = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
            preparedStmt2.setInt(1, tankDataForMap.getTank_id());
            int rowAffected2 = preparedStmt2.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {
            //  Thread.sleep(3000);
            return true;
        });

    }// Update Archive alarms  ****************************** Update Archive alarms   ***************************************

    //Update tanks table with tank level, tank_temperature , volume , volume percent and weight.
    private void updateTanksVolume(TankDataForMap tankDataForMap) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            //  System.out.println(tankDataForMap.getAlarm_description());
            String updateTanksVolumeH = "UPDATE tanks set tank_temperature = ?,tank_level = ?,weight =? ,volume_percent =? ,volume =?,high_alarm_limit =?,low_alarm_limit =?,density=? where tank_id = ? ;";
            PreparedStatement preparedStmt = conn.prepareStatement(updateTanksVolumeH, Statement.RETURN_GENERATED_KEYS);
            float max_volume = tankDataForMap.getMax_volume();
            float tank_temperature = (tankDataForMap.getMeanTemp() != 0.0f) ? tankDataForMap.getMeanTemp() : 0.0f;
            float density = tankDataForMap.getDensity();
            float volume = tankDataForMap.getVolume();
            float volume_percent = (max_volume > 0) ? (volume / max_volume) * 100 : 0;
            float weight = volume * density;

            preparedStmt.setFloat(1, tankDataForMap.getMeanTemp());
            preparedStmt.setFloat(2, tankDataForMap.getLevel());
            preparedStmt.setFloat(3, weight);
            preparedStmt.setFloat(4, volume_percent);
            preparedStmt.setFloat(5, volume);
            preparedStmt.setFloat(6, tankDataForMap.getTankHighLevel());
            preparedStmt.setFloat(7, tankDataForMap.getTankLowLevel());
            preparedStmt.setFloat(8, density);
            preparedStmt.setInt(9, tankDataForMap.getTank_id());
            int rowAffected = preparedStmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }//Update tanks table with tank level, tank_temperature , volume , volume percent and weight.

    // Insert new temp alarm  ****************************** Insert new temp alarm into alarms table   ***************************************
    public Future<Boolean> insertNewTempAlarm(TankDataForMap tankDataForMap) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String query = "INSERT INTO alarms (alarm_name,tank_id,acknowledged,alarm_description,archive,alarm_date,alarm_active,blue_alarm,temp_alarm) VALUES (?,?,?,?,?,?,?,?,?);";

            PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            // System.out.println(tankDataForMap);
            preparedStmt.setString(1, tankDataForMap.getTemp_alarm_name());
            preparedStmt.setInt(2, tankDataForMap.getTank_id());
            preparedStmt.setBoolean(3, tankDataForMap.isTemp_acknowledged());
            preparedStmt.setString(4, tankDataForMap.getTemp_alarm_description());
            preparedStmt.setBoolean(5, false);
            preparedStmt.setString(6, tankDataForMap.getTemp_alarm_date());
            preparedStmt.setBoolean(7, tankDataForMap.isTemp_alarm_active());
            preparedStmt.setBoolean(8, tankDataForMap.isTemp_blue_alarm());
            preparedStmt.setBoolean(9, true);

            int rowAffected = preparedStmt.executeUpdate();
            System.out.println(rowAffected + " Temp Alarm inserted");
            String updateTanks = "UPDATE tanks set alarm_name = ? where tank_id = ?;";
            PreparedStatement preparedStmt2 = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
            preparedStmt2.setString(1, tankDataForMap.getTemp_alarm_name());
            preparedStmt2.setInt(2, tankDataForMap.getTank_id());
            int rowAffected2 = preparedStmt2.executeUpdate();
        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {

            return true;
        });

    }// Insert new temp alarm  ****************************** Insert new temp alarm into alarms table   ***************************************

    // Update temp alarm  ****************************** Update temp alarm   ***************************************
    public Future<Boolean> updateTempAlarm(TankDataForMap tankDataForMap) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String query = "UPDATE alarms set alarm_description= ?,blue_alarm = ?,alarm_name = ?,alarm_date = ?,time_retrieved = ?,alarm_active = ?,time_accepted = ?,archive =?, acknowledged= ? where tank_id = ? && (alarm_active = 1 || blue_alarm = 1) && (temp_alarm = 1);";

            PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, tankDataForMap.getTemp_alarm_description());
            preparedStmt.setBoolean(2, tankDataForMap.isTemp_blue_alarm());
            preparedStmt.setString(3, tankDataForMap.getTemp_alarm_name());
            preparedStmt.setString(4, tankDataForMap.getTemp_alarm_date());
            preparedStmt.setString(5, tankDataForMap.getTemp_time_retrieved());
            preparedStmt.setBoolean(6, tankDataForMap.isTemp_alarm_active());
            preparedStmt.setString(7, tankDataForMap.getTemp_time_accepted());
            preparedStmt.setBoolean(8, tankDataForMap.isTemp_archive());
            preparedStmt.setBoolean(9, tankDataForMap.isTemp_acknowledged());
            preparedStmt.setInt(10, tankDataForMap.getTank_id());


            int rowAffected = preparedStmt.executeUpdate();
            System.out.println(rowAffected + " Temp Alarm updated.");
            String updateTanks = "UPDATE tanks set alarm_name = ? where tank_id = ?;";
            PreparedStatement preparedStmt2 = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
            preparedStmt2.setString(1, tankDataForMap.getAlarm_name());
            preparedStmt2.setInt(2, tankDataForMap.getTank_id());
            int rowAffected2 = preparedStmt2.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {

            return true;
        });

    } // Update temp alarm  ****************************** Update temp alarm   ***************************************

    // Check for temperature alarm ************************************** Check for temperature alarm ****************************
    public void manageTemperatureAlarms(TankDataForMap tankDataForMap) {
        //  System.out.println("Now in manage Temp alarms function");
        if (tankDataForMap.getTank_id() == 30) {
           //    System.out.println(tankDataForMap);
        }

        if (tankDataForMap.getMeanTemp() > tankDataForMap.getTemperature_limit()) {
            if (tankDataForMap.isUpdate_Temperature_alarm()) {
                tankDataForMap.setUpdate_Temperature_alarm(false);
                String temp_alarm_name = tankDataForMap.getCode_name() + " High Temp Alarm";
                tankDataForMap.setTemp_alarm_active(true);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String temperatureAlarmDate = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                tankDataForMap.setTemp_alarm_date(temperatureAlarmDate);
                tankDataForMap.setTemp_time_retrieved(null);
                tankDataForMap.setTemp_alarm_name(temp_alarm_name);
                tankDataForMap.setTime_accepted(null);
                tankDataForMap.setTemp_blue_alarm(false);
                tankDataForMap.setTemp_alarm_active(true);
                tankDataForMap.setTemp_acknowledged(false);
                tankDataForMap.setUpdate_Temperature_blue_alarm(true);
                tankDataForMap.setTemp_alarm_description("Active unaccepted High Temp Alarm triggered");
                if (tankDataForMap.isTemp_inserted()) {
                    updateTempAlarm(tankDataForMap);
                } else {
                    System.out.println("Temperature Alarm triggered");
                    //  insertNewTempAlarm(tankDataForMap);
                    tankDataForMap.setTemp_inserted(true);


                    boolean insertedOrNot = false;
                    try {
                        insertedOrNot = insertNewTempAlarm(tankDataForMap).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (insertedOrNot) {
                        tankDataForMap.setInserted(true);
                        System.out.println("Temp Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                    } else {
                        System.out.println("Error connecting database connect to db to update alarms");
                    }
                }

            }//if (tankDataForMap.isUpdate_Temperature_alarm())
        }// if (tankDataForMap.getMeanTemp() > tankDataForMap.getTemperature_limit())

        // Check for temperature alarm ******************************************* Check for temperature alarm *******************************

        // Check for Blue Temperature alarm ****************************************************
        if (tankDataForMap.getMeanTemp() < tankDataForMap.getTemperature_limit() && tankDataForMap.isTemp_alarm_active() == true && tankDataForMap.isTemp_acknowledged() == false) {
            if (tankDataForMap.isUpdate_Temperature_blue_alarm() == true) {
                System.out.println("Inside citerea");
                tankDataForMap.setUpdate_Temperature_blue_alarm(false);
                tankDataForMap.setUpdate_Temperature_alarm(true);
                tankDataForMap.setTemp_alarm_active(false);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String temperatureAlarmDate = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                tankDataForMap.setTemp_time_retrieved(temperatureAlarmDate);
                tankDataForMap.setTemp_blue_alarm(true);
                tankDataForMap.setTemp_alarm_description("Inactive unaccepted Temp Alarm");
                if (tankDataForMap.isTemp_inserted() == true) {
                    updateTempAlarm(tankDataForMap);
                }

            }// if (tankDataForMap.isUpdate_Temperature_blue_alarm() == true)

        }// Check for Blue Temperature alarm ****************************************************


        // Check for Archive Temperature alarm ****************************************************
        if (tankDataForMap.getMeanTemp() < tankDataForMap.getTemperature_limit() && tankDataForMap.isTemp_acknowledged() == true) {
            tankDataForMap.setUpdate_Temperature_blue_alarm(true);
            tankDataForMap.setUpdate_Temperature_alarm(true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String temperatureAlarmDate = LocalDateTime.now(Clock.systemUTC()).format(formatter);
            if (tankDataForMap.getTemp_time_retrieved() == null || tankDataForMap.getTemp_time_retrieved().length() == 0) {
                tankDataForMap.setTemp_time_retrieved(temperatureAlarmDate);
            }
            if (tankDataForMap.getTemp_time_accepted() == null || tankDataForMap.getTemp_time_accepted().length() == 0) {
                tankDataForMap.setTemp_time_accepted(temperatureAlarmDate);
            }
            tankDataForMap.setTemp_blue_alarm(false);
            tankDataForMap.setTemp_alarm_description("Archived Temp Alarm");
            tankDataForMap.setTemp_alarm_active(false);
            tankDataForMap.setTemp_archive(true);
            if (tankDataForMap.isTemp_inserted() == true) {
                boolean insertedOrNot = false;
                try {
                    insertedOrNot = updateTempAlarm(tankDataForMap).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (insertedOrNot) {
                    tankDataForMap.setTemp_inserted(false);
                    System.out.println("Temp Alarm for tank " + tankDataForMap.getCode_name() + " became archived");
                    tankDataForMap.setTemp_alarm_name(null);
                    tankDataForMap.setTemp_alarm_date(null);
                    tankDataForMap.setTemp_time_retrieved(null);
                    tankDataForMap.setTemp_time_accepted(null);
                    tankDataForMap.setTemp_alarm_description(null);
                    tankDataForMap.setTemp_archive(false);
                    tankDataForMap.setTemp_acknowledged(false);
                } else {
                    System.out.println("Error connecting database connect to db to update alarms");
                }
            }

        }// Check for Archive Temperature alarm ****************************************************


    } // public void manageTemperatureAlarms(TankDataForMap tankDataForMap)

    public void detectAlarms() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (TankLiveDataSubscription.tankSubscriptionData != null) {

                    //  System.out.println("***** Level Alarms server is running *****");

                    for (TankAlarmData tankAlarmData : TankLiveDataSubscription.tankSubscriptionData.getSetTankSubscriptionData()) {
                        TankDataForMap tankDataForMap = LavelMasterManager.tankMapData.get(tankAlarmData.getTankId());
                        if (tankDataForMap.getTank_id() == 11){
                            System.out.println(tankDataForMap);
                        }
                        tankDataForMap.setMeanTemp(tankAlarmData.getMeanTemp());
                        tankDataForMap.setLevel(tankAlarmData.getLevel());
                        tankDataForMap.setVolume(tankAlarmData.getVolume());

                        CompletableFuture.runAsync(() -> {
                            manageTemperatureAlarms(tankDataForMap);
                        });

                        CompletableFuture.runAsync(() -> {
                            updateTanksVolume(tankDataForMap);
                        });
                        if (tankAlarmData.getLevelAlarm() > 0) {
                            if (tankDataForMap.getAlarm_date() == null) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                tankDataForMap.setAlarm_date(alarmDateTriggered);
                            }
                            //    System.out.println("Tank Code: " + tankDataForMap.getCode_name() + " Is Inserted " + tankDataForMap.isInserted() + " Is Blue: " + tankDataForMap.isBlue_alarm() + " Is Active " + tankDataForMap.isAlarm_active() + " Level_alarm: " + tankDataForMap.getLevel_alarm() + " Description " + tankDataForMap.getAlarm_description() + " Alarm Date " + tankDataForMap.getAlarm_date() + " Time_retrieved " + tankDataForMap.getTime_retrieved());

                            // Check if tank level will reach beyond limits (High ,high high , low and low low) ********************************
                            if (tankDataForMap.getTankLowLowLevel() != -10 && tankDataForMap.getTankLowLevel() != -10) {
                                if ((tankAlarmData.getLevel() > tankDataForMap.getTankLowLevel())) {
                                    //   System.out.println(tankAlarmData.getLevel());
                                    //tru System.out.println("Tank Code: "+tankDataForMap.getCode_name()+" Low level is: "+tankDataForMap.getTankLowLevel()+" Low Low Level: "+tankDataForMap.getTankLowLowLevel());

                                }

                                //   System.out.println("Tank Code: "+tankDataForMap.getCode_name()+" Low level is: "+tankDataForMap.getTankLowLevel()+" Low Low Level: "+tankDataForMap.getTankLowLowLevel());

                                if ((tankAlarmData.getLevel() < tankDataForMap.getTankLowLevel()) && (tankAlarmData.getLevel() > tankDataForMap.getTankLowLowLevel())) {

                                    //   System.out.println(tankAlarmData.getLevel());
                                    if (tankDataForMap.isUpdateL() == true) {
                                        String alarmName = tankDataForMap.getCode_name() + " LevelAlarm L";
                                        tankDataForMap.setAlarm_active(true);
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                        tankDataForMap.setTime_retrieved(null);
                                        tankDataForMap.setAlarm_date(alarmDateTriggered);
                                        tankDataForMap.setAlarm_name(alarmName);
                                        tankDataForMap.setTime_accepted(null);
                                        tankDataForMap.setAcknowledged(false);
                                        tankDataForMap.setAlarm_description("Active unaccepted low Alarm triggered");
                                        tankDataForMap.setUpdateL(false);
                                        tankDataForMap.setUpdateH(true);
                                        tankDataForMap.setUpdateHH(true);
                                        tankDataForMap.setUpdateLL(true);
                                        tankDataForMap.setUpdateBlue(true);
                                        tankDataForMap.setBlue_alarm(false);
                                        tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                        LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                        try {
                                            if (tankDataForMap.isInserted()) {

                                                boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                                if (updatedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Low level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            } else {
                                                boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                                if (insertedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Low level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            }

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }


                                    }// (tankDataForMap.isUpdateL() == true) ****** Update for one time *************
                                } // ((tankAlarmData.getLevel() < tankDataForMap.getTankLowLevel()) && (tankAlarmData.getLevel() > tankDataForMap.getTankLowLowLevel()))

                                // If level alarm < Low Low level  ************************************
                                if ((tankAlarmData.getLevel() < tankDataForMap.getTankLowLowLevel())) {
                                    if (tankDataForMap.isUpdateLL() == true) {
                                        String alarmName = tankDataForMap.getCode_name() + " LevelAlarm LL";
                                        tankDataForMap.setAlarm_active(true);
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                        tankDataForMap.setTime_retrieved(null);
                                        tankDataForMap.setAlarm_date(alarmDateTriggered);
                                        tankDataForMap.setAlarm_name(alarmName);
                                        tankDataForMap.setTime_accepted(null);
                                        tankDataForMap.setAcknowledged(false);
                                        tankDataForMap.setAlarm_description("Active unaccepted Low Low Alarm triggered");
                                        tankDataForMap.setUpdateL(true);
                                        tankDataForMap.setUpdateH(true);
                                        tankDataForMap.setUpdateHH(true);
                                        tankDataForMap.setUpdateLL(false);
                                        tankDataForMap.setUpdateBlue(true);
                                        tankDataForMap.setBlue_alarm(false);
                                        tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                        LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                        try {
                                            if (tankDataForMap.isInserted()) {

                                                boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                                if (updatedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Low Low level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            } else {
                                                boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                                if (insertedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Low Low level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            }

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                    } // if (tankDataForMap.isUpdateLL() == true)
                                } // If level alarm < Low Low level  ************************************

                            }// tankDataForMap.getTankLowLowLevel() != -10 && tankDataForMap.getTankLowLevel() != -10

                            // (tankLowLowLevel != -10 && tankLowLevel == -10)
                            if (tankDataForMap.getTankLowLowLevel() != -10 && tankDataForMap.getTankLowLevel() == -10) {
                                if ((tankAlarmData.getLevel() < tankDataForMap.getTankLowLowLevel())) {
                                    if (tankDataForMap.isUpdateLL() == true) {
                                        String alarmName = tankDataForMap.getCode_name() + " LevelAlarm LL";
                                        tankDataForMap.setAlarm_active(true);
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                        tankDataForMap.setTime_retrieved(null);
                                        tankDataForMap.setAlarm_date(alarmDateTriggered);
                                        tankDataForMap.setAlarm_name(alarmName);
                                        tankDataForMap.setTime_accepted(null);
                                        tankDataForMap.setAcknowledged(false);
                                        tankDataForMap.setAlarm_description("Active unaccepted Low Low Alarm triggered");
                                        tankDataForMap.setUpdateL(true);
                                        tankDataForMap.setUpdateH(true);
                                        tankDataForMap.setUpdateHH(true);
                                        tankDataForMap.setUpdateLL(false);
                                        tankDataForMap.setUpdateBlue(true);
                                        tankDataForMap.setBlue_alarm(false);
                                        tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                        LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                        //   System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                        try {
                                            if (tankDataForMap.isInserted()) {

                                                boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                                if (updatedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Low Low level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            } else {
                                                boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                                if (insertedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Low Low level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            }

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                    } // if (tankDataForMap.isUpdateLL() == true)
                                } // If level alarm < Low Low level  ************************************


                            }// (tankDataForMap.getTankLowLowLevel() != -10 && tankDataForMap.getTankLowLevel() == -10)

                            if (tankDataForMap.getTankLowLevel() != -10 && tankDataForMap.getTankLowLowLevel() == -10) {
                                if ((tankAlarmData.getLevel() < tankDataForMap.getTankLowLevel())) {

                                    if (tankDataForMap.isUpdateL() == true) {
                                        String alarmName = tankDataForMap.getCode_name() + " LevelAlarm L";
                                        tankDataForMap.setAlarm_active(true);
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                        tankDataForMap.setTime_retrieved(null);
                                        tankDataForMap.setAlarm_date(alarmDateTriggered);
                                        tankDataForMap.setAlarm_name(alarmName);
                                        tankDataForMap.setTime_accepted(null);
                                        tankDataForMap.setAcknowledged(false);
                                        tankDataForMap.setAlarm_description("Active unaccepted low Alarm triggered");
                                        tankDataForMap.setUpdateL(false);
                                        tankDataForMap.setUpdateH(true);
                                        tankDataForMap.setUpdateHH(true);
                                        tankDataForMap.setUpdateLL(true);
                                        tankDataForMap.setUpdateBlue(true);
                                        tankDataForMap.setBlue_alarm(false);
                                        tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                        LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                        try {
                                            if (tankDataForMap.isInserted()) {

                                                boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                                if (updatedOrNot) {
                                                    //  tankDataForMap.setInserted(true);
                                                    System.out.println("Low level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            } else {
                                                boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                                if (insertedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Low level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            }

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }


                                    }// (tankDataForMap.isUpdateL() == true) ****** Update for one time *************
                                }
                            }//(tankDataForMap.getTankLowLevel() != -10 && tankDataForMap.getTankLowLowLevel() == -10)

                            // Check for high and high high tank level ********* Check for high and high high tank level
                            if ((tankAlarmData.getLevel() > tankDataForMap.getTankHighLevel()) && (tankAlarmData.getLevel() < tankDataForMap.getHighHighLevel())) {
                                if (tankDataForMap.isUpdateH() == true) {
                                    String alarmName = tankDataForMap.getCode_name() + " LevelAlarm H";
                                    tankDataForMap.setAlarm_active(true);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    tankDataForMap.setTime_retrieved(null);
                                    tankDataForMap.setAlarm_date(alarmDateTriggered);
                                    tankDataForMap.setAlarm_name(alarmName);
                                    tankDataForMap.setTime_accepted(null);
                                    tankDataForMap.setAcknowledged(false);
                                    tankDataForMap.setAlarm_description("Active unaccepted high Alarm triggered");
                                    tankDataForMap.setUpdateL(true);
                                    tankDataForMap.setUpdateH(false);
                                    tankDataForMap.setUpdateHH(true);
                                    tankDataForMap.setUpdateLL(true);
                                    tankDataForMap.setUpdateBlue(true);
                                    tankDataForMap.setBlue_alarm(false);
                                    tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                    LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                    try {
                                        if (tankDataForMap.isInserted()) {

                                            boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                            if (updatedOrNot) {
                                                tankDataForMap.setInserted(true);
                                                System.out.println("High level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                            } else {
                                                System.out.println("Error connecting database connect to db to update alarms");
                                            }
                                        } else {
                                            boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                            if (insertedOrNot) {
                                                tankDataForMap.setInserted(true);
                                                System.out.println("High level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                            } else {
                                                System.out.println("Error connecting database connect to db to update alarms");
                                            }
                                        }

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }


                                }// (tankDataForMap.isUpdateH() == true) ****** Update for one time *************
                            }//((tankAlarmData.getLevel() > tankDataForMap.getTankHighLevel()) && (tankAlarmData.getLevel() < tankDataForMap.getHighHighLevel()))

                            if ((tankAlarmData.getLevel() > tankDataForMap.getHighHighLevel())) {
                                if (tankDataForMap.isUpdateHH() == true) {
                                    String alarmName = tankDataForMap.getCode_name() + " LevelAlarm HH";
                                    tankDataForMap.setAlarm_active(true);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    tankDataForMap.setTime_retrieved(null);
                                    tankDataForMap.setAlarm_date(alarmDateTriggered);
                                    tankDataForMap.setAlarm_name(alarmName);
                                    tankDataForMap.setTime_accepted(null);
                                    tankDataForMap.setAcknowledged(false);
                                    tankDataForMap.setAlarm_description("Active unaccepted high high Alarm triggered");
                                    tankDataForMap.setUpdateL(true);
                                    tankDataForMap.setUpdateH(true);
                                    tankDataForMap.setUpdateHH(false);
                                    tankDataForMap.setUpdateLL(true);
                                    tankDataForMap.setUpdateBlue(true);
                                    tankDataForMap.setBlue_alarm(false);
                                    tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                    LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                    try {
                                        if (tankDataForMap.isInserted()) {

                                            boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                            if (updatedOrNot) {
                                                tankDataForMap.setInserted(true);
                                                System.out.println("High high level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                            } else {
                                                System.out.println("Error connecting database connect to db to update alarms");
                                            }
                                        } else {
                                            boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                            if (insertedOrNot) {
                                                tankDataForMap.setInserted(true);
                                                System.out.println("High high level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                            } else {
                                                System.out.println("Error connecting database connect to db to update alarms");
                                            }
                                        }

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }


                                }// (tankDataForMap.isUpdateHH() == true)  ****** Update for one time *************
                            }//if ((tankAlarmData.getLevel() > tankDataForMap.getHighHighLevel()))
                            // Check for high and high high tank level ********* Check for high and high high tank level
                            // Check if tank level will reach beyond limits (High ,high high , low and low low) ********************************

                            //***** Check if alarm comes from alarm state to normal state without being accepted...***********
                            if ((tankAlarmData.getLevel() > tankDataForMap.getTankLowLevel() && tankAlarmData.getLevel() < tankDataForMap.getTankHighLevel()) && (tankDataForMap.isAcknowledged() == false)) {
                                if (tankDataForMap.getAlarm_name() == null) {
                                    switch (tankAlarmData.getLevelAlarm()) {

                                        case 17:
                                        case 16:
                                        case 25:
                                        case 24:
                                        case 27:
                                        case 29:
                                        case 31:
                                            tankDataForMap.setAlarm_name(tankDataForMap.getCode_name() + " LevelAlarm" + " HH");
                                            break;
                                        case 15:
                                        case 13:
                                        case 11:
                                        case 9:
                                        case 8:
                                            tankDataForMap.setAlarm_name(tankDataForMap.getCode_name() + " LevelAlarm" + " H");
                                            break;
                                        case 7:
                                        case 6:
                                        case 3:
                                        case 2:
                                            tankDataForMap.setAlarm_name(tankDataForMap.getCode_name() + " LevelAlarm" + " LL");
                                            break;
                                        case 5:
                                        case 4:
                                            tankDataForMap.setAlarm_name(tankDataForMap.getCode_name() + " LevelAlarm" + " L");
                                            break;

                                        default:
                                            tankDataForMap.setAlarm_name(tankDataForMap.getCode_name() + " LevelAlarm" + " HL");
                                            break;
                                    }
                                }
                                if (tankDataForMap.getTankLowLevel() != -10 && tankDataForMap.getTankLowLowLevel() != -10) {
                                    if (tankDataForMap.getTankLowLevel() != 0) {
                                        if (tankDataForMap.isUpdateBlue() == true) {
                                            tankDataForMap.setUpdateL(true);
                                            tankDataForMap.setUpdateH(true);
                                            tankDataForMap.setUpdateHH(true);
                                            tankDataForMap.setUpdateLL(true);
                                            tankDataForMap.setUpdateBlue(false);
                                            tankDataForMap.setBlue_alarm(true);
                                            tankDataForMap.setAlarm_active(false);
                                            tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                            String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                            tankDataForMap.setTime_retrieved(alarmDateTriggered);
                                            tankDataForMap.setAlarm_description("Inactive unaccepted");
                                            // LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                            try {
                                                if (tankDataForMap.isInserted()) {

                                                    boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                                    if (updatedOrNot) {
                                                        tankDataForMap.setInserted(true);
                                                        System.out.println("Blue level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                                    } else {
                                                        System.out.println("Error connecting database connect to db to update alarms");
                                                    }
                                                } else {
                                                    boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                                    if (insertedOrNot) {
                                                        tankDataForMap.setInserted(true);
                                                        System.out.println("Inactive unaccepted level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                                    } else {
                                                        System.out.println("Error connecting database connect to db to update alarms");
                                                    }
                                                }

                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                                //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            }

                                        } //  if (tankDataForMap.isUpdateBlue() == true)
                                    }//if (tankDataForMap.getTankLowLevel() != 0)


                                }//if (tankDataForMap.getTankLowLevel() != -10 && tankDataForMap.getTankLowLowLevel() != -10)

                                if (tankDataForMap.getTankLowLevel() == -10 && tankAlarmData.getLevel() > tankDataForMap.getTankLowLowLevel()) {
                                    if (tankDataForMap.isUpdateBlue() == true) {
                                        tankDataForMap.setUpdateL(true);
                                        tankDataForMap.setUpdateH(true);
                                        tankDataForMap.setUpdateHH(true);
                                        tankDataForMap.setUpdateLL(true);
                                        tankDataForMap.setUpdateBlue(false);
                                        tankDataForMap.setBlue_alarm(true);
                                        tankDataForMap.setAlarm_active(false);
                                        tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                        tankDataForMap.setTime_retrieved(alarmDateTriggered);
                                        tankDataForMap.setAlarm_description("Inactive unaccepted");
                                        LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                        try {
                                            if (tankDataForMap.isInserted()) {

                                                boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                                if (updatedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Inactive unaccepted level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            } else {
                                                boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                                if (insertedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("High high level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            }

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }

                                    }// if (tankDataForMap.isUpdateBlue() == true)

                                }// if (tankDataForMap.getTankLowLevel() == -10 && tankAlarmData.getLevel() > tankDataForMap.getTankLowLowLevel())

                                if (tankDataForMap.getTankLowLowLevel() == -10 && tankAlarmData.getLevel() > tankDataForMap.getTankLowLevel()) {
                                    if (tankDataForMap.isUpdateBlue() == true) {
                                        tankDataForMap.setUpdateL(true);
                                        tankDataForMap.setUpdateH(true);
                                        tankDataForMap.setUpdateHH(true);
                                        tankDataForMap.setUpdateLL(true);
                                        tankDataForMap.setUpdateBlue(false);
                                        tankDataForMap.setBlue_alarm(true);
                                        tankDataForMap.setAlarm_active(false);
                                        tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                        tankDataForMap.setTime_retrieved(alarmDateTriggered);
                                        tankDataForMap.setAlarm_description("Inactive unaccepted");
                                        LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                        try {
                                            if (tankDataForMap.isInserted()) {

                                                boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                                if (updatedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("Inactive unaccepted level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            } else {
                                                boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                                if (insertedOrNot) {
                                                    tankDataForMap.setInserted(true);
                                                    System.out.println("High high level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                                } else {
                                                    System.out.println("Error connecting database connect to db to update alarms");
                                                }
                                            }

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }

                                    }// if (tankDataForMap.isUpdateBlue() == true)
                                }

                            }//***** Check if alarm comes from alarm state to normal state without being accepted...***********

                            // Check if alarm accepted or not ******************* Check if alarm accepted or not ************************
                            if (tankAlarmData.getLevelAlarm() % 2 == 1) {
                                tankDataForMap.setAcknowledged(false);
                                //  LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(),tankDataForMap);
                            } else {
                                if (!tankDataForMap.isAcknowledged()) {
                                    tankDataForMap.setAcknowledged(true);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String time_accepted = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    tankDataForMap.setTime_accepted(time_accepted);
                                    tankDataForMap.setAlarm_description("Active accepted");
                                    tankDataForMap.setLevel_alarm(tankAlarmData.getLevelAlarm());
                                    LavelMasterManager.tankMapData.put(tankDataForMap.getTank_id(), tankDataForMap);
                                    try {
                                        if (tankDataForMap.isInserted()) {

                                            boolean updatedOrNot = updateLevelAlarm(tankDataForMap).get();
                                            if (updatedOrNot) {
                                                tankDataForMap.setInserted(true);
                                                System.out.println("Active accepted level Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                            } else {
                                                System.out.println("Error connecting database connect to db to update alarms");
                                            }
                                        } else {
                                            boolean insertedOrNot = insertNewLevelAlarm(tankDataForMap).get();
                                            if (insertedOrNot) {
                                                tankDataForMap.setInserted(true);
                                                System.out.println("Active accepted level Alarm for tank " + tankDataForMap.getCode_name() + " inserted");
                                            } else {
                                                System.out.println("Error connecting database connect to db to update alarms");
                                            }
                                        }

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } // Check if alarm accepted or not ******************* Check if alarm accepted or not ************************


                        } else {// tankAlarmData.getLevel() > 0 ************************ If Level Alarm > 0  *****************

                            if (tankDataForMap.isAcknowledged() || (tankDataForMap.isBlue_alarm())) {
                                if (tankDataForMap.getTime_retrieved() == null) {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String time_retrieved = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    tankDataForMap.setTime_retrieved(time_retrieved);
                                }
                                if (tankDataForMap.getTime_accepted() == null) {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String time_accepted = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    tankDataForMap.setTime_accepted(time_accepted);
                                }
                                tankDataForMap.setAlarm_description("Archived Alarm");
                                tankDataForMap.setAlarm_active(false);
                                tankDataForMap.setArchive(true);
                                tankDataForMap.setBlue_alarm(false);
                                try {

                                    boolean updatedOrNot = updateArchivedAlarm(tankDataForMap).get();
                                    if (updatedOrNot) {
                                        tankDataForMap.setArchive(false);
                                        tankDataForMap.setUpdateH(true);
                                        tankDataForMap.setUpdateHH(true);
                                        tankDataForMap.setUpdateL(true);
                                        tankDataForMap.setUpdateLL(true);
                                        tankDataForMap.setUpdateBlue(true);
                                        tankDataForMap.setInserted(false);
                                        tankDataForMap.setAcknowledged(false);
                                        tankDataForMap.setLevel_alarm(0);
                                        tankDataForMap.setAlarm_name(null);
                                        tankDataForMap.setAlarm_description(null);
                                        tankDataForMap.setBlue_alarm(false);
                                        tankDataForMap.setAlarm_date(null);
                                        tankDataForMap.setTime_accepted(null);
                                        tankDataForMap.setTime_retrieved(null);


                                        System.out.println("Archive Alarm for tank " + tankDataForMap.getCode_name() + " updated");
                                    } else {
                                        System.out.println("Error connecting database connect to db to update alarms");
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    //  System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                            } // if (tankDataForMap.isAcknowledged() )


                        }//tankAlarmData.getLevel() == 0 ************************ If Level Alarm == 0  ***************** No Alarm in this case

                    }// Iteration of tanks live data subscription list coming from ksl  ***************************************************


                }//if (TankLiveDataSubscription.tankSubscriptionData != null)


            }
        }, 400, 3000);
    }
}
