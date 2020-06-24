package com.kockumation.backEnd.levelMaster;

import com.kockumation.backEnd.levelMaster.TankLiveDataSubscription;
import com.kockumation.backEnd.levelMaster.model.KslTankData;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        System.out.println("Inside DetectAndSave Alarms " + Thread.currentThread());
        while (!Thread.currentThread().isInterrupted() && firstRun) {
            firstRun = false;
            detectAlarms();
        }
    }// Run function ****************  Run function ******************


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
            String updateAlarms = "UPDATE alarms set alarm_description = ?,blue_alarm = ?,time_retrieved =? ,alarm_name =? ,alarm_date =?,alarm_active =?,time_accepted =?,acknowledged=? where tank_id = ? && (temp_alarm = 0) && (archive = 0);";
            PreparedStatement preparedStmt = conn.prepareStatement(updateAlarms, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, tankDataForMap.getAlarm_description());
            preparedStmt.setBoolean(2, tankDataForMap.isBlue_alarm());
            preparedStmt.setString(3, tankDataForMap.getTime_retrieved());
            preparedStmt.setString(4, tankDataForMap.getAlarm_name());
            preparedStmt.setString(5, tankDataForMap.getAlarm_date());
            preparedStmt.setBoolean(6, tankDataForMap.isAlarm_active());
            preparedStmt.setString(7, tankDataForMap.getTime_accepted());
            preparedStmt.setBoolean(8, tankDataForMap.isAcknowledged());
            preparedStmt.setInt(9, tankDataForMap.getTank_id());
            int rowAffected = preparedStmt.executeUpdate();

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


    public void detectAlarms() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (TankLiveDataSubscription.tankSubscriptionData != null) {
                    //    System.out.println(TankLiveDataSubscription.tankSubscriptionData.getSetTankSubscriptionData());
                    for (TankAlarmData tankAlarmData : TankLiveDataSubscription.tankSubscriptionData.getSetTankSubscriptionData()) {
                        TankDataForMap tankDataForMap = LavelMasterManager.tankMapData.get(tankAlarmData.getTankId());
                        if (tankAlarmData.getLevel() > 0) {

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
                                        System.out.println(LavelMasterManager.tankMapData.get(tankAlarmData.getTankId()));
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
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                            String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                            tankDataForMap.setTime_retrieved(alarmDateTriggered);
                                            tankDataForMap.setAlarm_description("Inactive unaccepted");
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
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                        tankDataForMap.setTime_retrieved(alarmDateTriggered);
                                        tankDataForMap.setAlarm_description("Inactive unaccepted");

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
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                        String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                        tankDataForMap.setTime_retrieved(alarmDateTriggered);
                                        tankDataForMap.setAlarm_description("Inactive unaccepted");

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



                        }// tankAlarmData.getLevel() > 0 ************************ If Level Alarm > 0  *****************

                    }// Iteration of tanks live data subscription list coming from ksl  ***************************************************


                }


            }
        }, 400, 3000);
    }
}
