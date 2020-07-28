package com.kockumation.backEnd.ValvesMaster;

import com.kockumation.backEnd.ValvesMaster.model.ValveAlarmData;
import com.kockumation.backEnd.ValvesMaster.model.ValveDataForMap;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.kockumation.backEnd.levelMaster.LiveDataWebsocketClient.valvesSubscriptionData;

public class DetectAndSaveValvesAlarms {
    public static Timer timer;
    public static boolean firstRun = true;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();

    public DetectAndSaveValvesAlarms() {

    }

    // Insert new valve Alarm into alarms table ****************************** Insert new valve Alarm into alarms table   ***************************************
    public Future<Boolean> insertNewValveAlarm(ValveDataForMap valveDataForMap) {
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            String query = "INSERT INTO alarms (alarm_name,valve_id,acknowledged,alarm_description,valve_status,alarm_date,time_accepted,blue_alarm,time_retrieved) VALUES (?,?,?,?,?,?,?,?,?,?,?);";

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
            //  String query = "INSERT INTO alarms (alarm_name,tank_id,acknowledged,alarm_description,level_alarm,archive,alarm_date,time_accepted,alarm_active,blue_alarm,time_retrieved) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
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

            int rowAffected = preparedStmt.executeUpdate();

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

    }// Update Valve alarm  ****************************** Update Valve alarm   ***************************************




    public void createNewTimer() {
        timer = new Timer();
        detectAlarms();
    }

    // Detect Valves alarms ******************************************
    public void detectAlarms() {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Inside Timer.....");
           /*     if (valvesSubscriptionData != null) {

                    for (ValveAlarmData valveAlarmData : valvesSubscriptionData.getSetSmValveSubscriptionData()) {
                        System.out.println(valveAlarmData);
                    }

                }*/


            }

        }, 2000, 3000);
    }// Detect Valves alarms ******************************************




}
