package com.kockumation.backEnd.ValvesMaster;

import com.kockumation.backEnd.ValvesMaster.model.ValveAlarmData;
import com.kockumation.backEnd.ValvesMaster.model.ValveDataForMap;
import com.kockumation.backEnd.levelMaster.LiveDataWebsocketClient;

import com.kockumation.backEnd.global.Db;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class DetectAndSaveValvesAlarms {
    public boolean isFirstRun;
    public static Timer timer;
    public static boolean firstRun = true;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();
    public Map<Integer, String> valvesDescription;
    ValvesMasterManager valvesMasterManager;

    public DetectAndSaveValvesAlarms() {
        valvesDescription = new HashMap<>();
        isFirstRun = true;
        valvesMasterManager = new ValvesMasterManager();
    }

    // Insert new valve Alarm into alarms table ****************************** Insert new valve Alarm into alarms table   ***************************************
    public Future<Boolean> insertNewValveAlarm(ValveDataForMap valveDataForMap) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String query = "INSERT INTO alarms (alarm_name,valve_id,acknowledged,alarm_description,valve_status,alarm_date,time_accepted,blue_alarm,time_retrieved) VALUES (?,?,?,?,?,?,?,?,?);";

            PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, valveDataForMap.getAlarm_name());
            preparedStmt.setInt(2, valveDataForMap.getValve_id());
            preparedStmt.setBoolean(3, valveDataForMap.isAcknowledged());
            preparedStmt.setString(4, valveDataForMap.getAlarm_description());
            preparedStmt.setInt(5, valveDataForMap.getValve_status());

            preparedStmt.setString(6, valveDataForMap.getAlarm_date());
            preparedStmt.setString(7, valveDataForMap.getTime_accepted());

            preparedStmt.setBoolean(8, valveDataForMap.isBlue_alarm());
            preparedStmt.setString(9, valveDataForMap.getTime_retrieved());
            int rowAffected = preparedStmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {

            return true;
        });

    }// Insert new valve Alarm into alarms table ****************************** Insert new valve Alarm into alarms table   ***************************************

    // Update Valve alarm  ****************************** Update Valve alarm   ***************************************
    public Future<Boolean> updateValveAlarm(ValveDataForMap valveDataForMap) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            String updateAlarms = "UPDATE alarms set alarm_name= ?,alarm_description = ?,blue_alarm = ?,alarm_date =? ,time_retrieved =?,alarm_active =?,time_accepted =?,acknowledged=?,valve_status = ? where (valve_id = ? && (archive = 0));";
            PreparedStatement preparedStmt = conn.prepareStatement(updateAlarms, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, valveDataForMap.getAlarm_name());
            preparedStmt.setString(2, valveDataForMap.getAlarm_description());
            preparedStmt.setBoolean(3, valveDataForMap.isBlue_alarm());
            preparedStmt.setString(4, valveDataForMap.getAlarm_date());
            preparedStmt.setString(5, valveDataForMap.getTime_retrieved());
            preparedStmt.setBoolean(6, valveDataForMap.isAlarm_active());
            preparedStmt.setString(7, valveDataForMap.getTime_accepted());
            preparedStmt.setBoolean(8, valveDataForMap.isAcknowledged());
            preparedStmt.setInt(9, valveDataForMap.getValve_status());
            preparedStmt.setInt(10, valveDataForMap.getValve_id());
            int rowAffected = preparedStmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {

            return true;
        });

    }// Update Valve alarm  ****************************** Update Valve alarm   ***************************************

    // Update Archive Valve alarm  ****************************** Update Archive Valve alarm   ***************************************
    public Future<Boolean> updateArchiveValveAlarm(ValveDataForMap valveDataForMap) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            String updateAlarms = "UPDATE alarms set alarm_description = ?,blue_alarm = ?,time_retrieved =?,alarm_active =?,time_accepted =?,archive = 1 where (valve_id = ? && (alarm_active = 1 || blue_alarm = 1));";
            PreparedStatement preparedStmt = conn.prepareStatement(updateAlarms, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, valveDataForMap.getAlarm_description());
            preparedStmt.setBoolean(2, valveDataForMap.isBlue_alarm());
            preparedStmt.setString(3, valveDataForMap.getTime_retrieved());
            preparedStmt.setBoolean(4, valveDataForMap.isAlarm_active());
            preparedStmt.setString(5, valveDataForMap.getTime_accepted());
            preparedStmt.setInt(6, valveDataForMap.getValve_id());

            int rowAffected = preparedStmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                return false;
            });
        }
        return executor.submit(() -> {
            return true;
        });

    }// Update Archive Valve alarm  ****************************** Update Archive Valve alarm   ***************************************

    public void createNewTimer() {
        timer = new Timer();
        detectAlarms();
    }

    // Detect Valves alarms ******************************************
    public void detectAlarms() {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Valves server running..");

                if (LiveDataWebsocketClient.valvesSubscriptionData != null) {

                    for (ValveAlarmData valveAlarmData : LiveDataWebsocketClient.valvesSubscriptionData.getSetSmValveSubscriptionData()) {
                        int valve_id = valveAlarmData.getId();
                        int valve_status = valveAlarmData.getStatus();

                        if (Db.valveMapData.containsKey(valve_id)) {
                            ValveDataForMap valveDataForMap = Db.valveMapData.get(valve_id);
                            valveDataForMap.setValve_status(valve_status);
                          /*  if (valveDataForMap.getValve_id() == 1){
                                System.out.println(valveDataForMap);
                            }*/
                            String statusDescription = "";



                            switch (valve_status) {
                                case 1:
                                    statusDescription = "OPENED";
                                    break;
                                case 2:
                                    statusDescription = "CLOSED";
                                    break;
                                case 4:
                                    statusDescription = "Moving to OPENED position";
                                    break;
                                case 8:
                                    statusDescription = "Moving to CLOSED position";
                                    break;
                                case 32:
                                    statusDescription = "Manual mode, valve is controlled by somebody else";
                                    break;
                                case 33:
                                    statusDescription = "Valve is open but is not controlled by us";
                                    break;
                                case 34:
                                    statusDescription = "Valve is closed but is not controlled by us";
                                    break;
                                case 36:
                                    statusDescription = "Valve is moving to open position but is not controlled by us";
                                    break;
                                case 40:
                                    statusDescription = "Valve is moving to closed position but is not controlled by us";
                                    break;
                                case 48:
                                    statusDescription = "Error but is not controlled by us";
                                    break;

                                default:
                                    statusDescription = "Error";
                            }

                            // Check for valve alarm  **********************************************************************************************
                            if (valveDataForMap.isUpdateRed()) {
                                boolean updateRed = false;

                                if ((valveDataForMap.getValve_status() >= 16 && valveDataForMap.getValve_type() == 1 && !valveDataForMap.isAlarm_active()) || ((valveDataForMap.getSubType() == 100 && valve_status == 1 && !valveDataForMap.isAlarm_active())) || ((valveDataForMap.getValve_type() == 4 && valveDataForMap.getValve_status() == 0 && !valveDataForMap.isAlarm_active()))) {
                                    System.out.println("Valve: " + valveDataForMap.getValve_name() + " has got unaccepted alarm .");
                                    if (isFirstRun) {
                                        valveDataForMap.setAlarm_description("Active unaccepted error during moving");
                                    } else {

                                        valveDataForMap.setAlarm_description("Active unaccepted error during " + valvesDescription.get(valve_id));
                                    }
                                    valveDataForMap.setAlarm_name(valveDataForMap.getValve_name());
                                    if (valve_id == 98 || valve_id == 99) {
                                        valveDataForMap.setAlarm_name("Offline plc");
                                        valveDataForMap.setAlarm_description("Offline plc");
                                    }

                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String alarmDateTriggered = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    valveDataForMap.setAlarm_date(alarmDateTriggered);

                                    valveDataForMap.setTime_retrieved(null);
                                    valveDataForMap.setAlarm_active(true);
                                    valveDataForMap.setValve_status(valve_status);
                                    valveDataForMap.setBlue_alarm(false);
                                    valveDataForMap.setTime_retrieved(null);
                                    if (valveDataForMap.getValve_type() == 4 && valve_status == 0) {
                                        valveDataForMap.setAlarm_description("Active unaccepted error");
                                    }
                                    valveDataForMap.setUpdateRed(false);
                                    valveDataForMap.setUpdateBlue(true);

                                    if (valveDataForMap.isInserted()) {
                                        try {
                                            updateRed = updateValveAlarm(valveDataForMap).get();
                                            if (updateRed) {
                                                valveDataForMap.setInserted(true);
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        insertNewValveAlarm(valveDataForMap);
                                        valveDataForMap.setInserted(true);
                                    }

                                }// if ((valve_status>=16) || ((valveDataForMap.getSubType() == 100 && valve_status == 1)) || ((valveDataForMap.getValve_type() == 4 && valve_status == 0))) {
                            } // If isUpdateRed **********

                            //Blue Alarm Valve type == 1
                            if (valveDataForMap.isUpdateBlue()) {
                                if ((valveDataForMap.getValve_status() < 16 && valveDataForMap.getValve_type() == 1 && valveDataForMap.getSubType() != 100 && valve_status != 48) && (valveDataForMap.isAcknowledged() == false && valveDataForMap.isUpdateBlue() == true && valveDataForMap.isAlarm_active() == true)) {
                                    boolean updateBlueAlarm;
                                    //  System.out.println("Valve Blue Alarm detected.");
                                    valveDataForMap.setUpdateBlue(false);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String time_retrieved = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    valveDataForMap.setAlarm_active(false);
                                    valveDataForMap.setBlue_alarm(true);
                                    valveDataForMap.setTime_retrieved(time_retrieved);
                                    valveDataForMap.setUpdateRed(true);
                                    valveDataForMap.setAlarm_description("Inactive unaccepted");
                                    try {
                                        updateBlueAlarm = updateValveAlarm(valveDataForMap).get();
                                        if (updateBlueAlarm)
                                            System.out.println("Valve: " + valveDataForMap.getValve_name() + " has got blue alarm.");
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }//Blue Alarm Valve type == 1

                            //Blue Alarm Type 4
                            if (valveDataForMap.isUpdateBlue()) {
                                if ((valveDataForMap.getValve_status() != 0 && valveDataForMap.getValve_type() == 4) && (valveDataForMap.isAcknowledged() == false && valveDataForMap.isAlarm_active() == true)) {
                                    boolean updateBlueAlarm;
                                    valveDataForMap.setUpdateBlue(false);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String time_retrieved = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    valveDataForMap.setAlarm_active(false);
                                    valveDataForMap.setBlue_alarm(true);
                                    valveDataForMap.setTime_retrieved(time_retrieved);
                                    valveDataForMap.setUpdateRed(true);
                                    valveDataForMap.setAlarm_description("Inactive unaccepted");
                                    try {
                                        updateBlueAlarm = updateValveAlarm(valveDataForMap).get();
                                        if (updateBlueAlarm)
                                            System.out.println("Valve: " + valveDataForMap.getValve_id() + " Becomes blue alarm.");
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }//Blue Alarm Type 4

                            //Blue Alarm SubType 100
                            if (valveDataForMap.isUpdateBlue()) {
                                if ((valveDataForMap.getSubType() == 100 && valveDataForMap.getValve_status() == 0) && (valveDataForMap.isAcknowledged() == false && valveDataForMap.isAlarm_active() == true)) {
                                    valveDataForMap.setUpdateBlue(false);
                                    boolean updateBlueAlarm;
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String time_retrieved = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    valveDataForMap.setAlarm_active(false);
                                    valveDataForMap.setBlue_alarm(true);
                                    valveDataForMap.setTime_retrieved(time_retrieved);
                                    valveDataForMap.setUpdateRed(true);
                                    valveDataForMap.setAlarm_description("Inactive unaccepted");

                                    try {
                                        updateBlueAlarm = updateValveAlarm(valveDataForMap).get();
                                        if (updateBlueAlarm)
                                            System.out.println("Valve: " + valveDataForMap.getValve_id() + "Becomes blue alarm.");
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }//Blue Alarm SubType 100

                            //Alarm becomes archive
                            if (((valveDataForMap.getValve_status() < 16 && valveDataForMap.getValve_type() != 4 && valveDataForMap.getSubType() != 100) && valveDataForMap.isAcknowledged() == true) || ((valveDataForMap.getValve_status() == 0 && valveDataForMap.getSubType() == 100) && valveDataForMap.isAcknowledged() == true) || ((valveDataForMap.getValve_status() != 0 && valveDataForMap.getValve_type() == 4) && valveDataForMap.isAcknowledged() == true)) {
                                boolean updateArchive = false;
                                if (valveDataForMap.getTime_retrieved() == null) {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    String time_retrieved = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                                    valveDataForMap.setTime_retrieved(time_retrieved);
                                }
                                valveDataForMap.setArchive(true);
                                valveDataForMap.setAlarm_description("Archived Alarm");
                                valveDataForMap.setBlue_alarm(false);
                                valveDataForMap.setAlarm_active(false);

                                try {
                                    updateArchive = updateArchiveValveAlarm(valveDataForMap).get();

                                    if (updateArchive) {
                                        System.out.println("Valve: " + valveDataForMap.getValve_name() + " Alarm Became Archive");
                                        valveDataForMap.setArchive(false);
                                        valveDataForMap.setAlarm_description(null);
                                        valveDataForMap.setInserted(false);
                                        valveDataForMap.setAcknowledged(false);
                                        valveDataForMap.setTime_accepted(null);
                                        valveDataForMap.setTime_retrieved(null);
                                        valveDataForMap.setAlarm_description(null);
                                        valveDataForMap.setAlarm_date(null);
                                        valveDataForMap.setUpdateRed(true);
                                        valveDataForMap.setUpdateBlue(true);
                                        valveDataForMap.setAlarm_name(null);
                                        valveDataForMap.setValve_status(0);

                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                            //Alarm becomes archive


                            valvesDescription.put(valve_id, statusDescription);


                        }// If mapData hasKey


                    } // for (ValveAlarmData valveAlarmData : valvesSubscriptionData.getSetSmValveSubscriptionData())

                    isFirstRun = false;
                } // if (valvesSubscriptionData != null)
            } //   timer.scheduleAtFixedRate(new TimerTask() {  @Override public void run()


        }, 2000, 4000);
    }// Detect Valves alarms ******************************************


}
