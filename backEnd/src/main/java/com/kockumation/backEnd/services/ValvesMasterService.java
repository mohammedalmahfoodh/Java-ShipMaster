package com.kockumation.backEnd.services;

import com.kockumation.backEnd.ValvesMaster.ValvesMasterManager;
import com.kockumation.backEnd.ValvesMaster.model.ValveDataForMap;
import com.kockumation.backEnd.global.GlobalVariableSingleton;
import com.kockumation.backEnd.levelMaster.LevelMasterManager;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;
import com.kockumation.backEnd.model.LevelPostObject;
import com.kockumation.backEnd.model.ValvePostObject;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ValvesMasterService {
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();
    private final String uri = "ws://192.168.190.232:8089";
    //  private final String uri = "ws://127.0.0.1:8089";
 WebSocketClient webSocketClient;
    public static boolean ifValveAcknowledged = false;
    public ValvesMasterService() {
        webSocketClient = new WebSocketClient();
    }

    // Make Valve alarm Acknowledged *************** Make Valve alarm Acknowledged
    public Future<Boolean> makeAlarmAcknowledged(ValvePostObject valvePostObject) {
        if (ValvesMasterManager.valveMapData.containsKey(valvePostObject.getValve_id())) {
            ValveDataForMap valveDataForMap = ValvesMasterManager.valveMapData.get(valvePostObject.getValve_id());

            if (valveDataForMap.getSubType() == 100){
                String setAcceptValveSensor = "setAcceptLevelSensors";
                String setAcceptValveSensorString ="{"+setAcceptValveSensor+":{}}";
                try {
                    GlobalVariableSingleton.getInstance().getClient().connectToServer(webSocketClient, new URI(uri));
                    webSocketClient.sendMessage(setAcceptValveSensorString);
                    try {
                        ifValveAcknowledged = webSocketClient.onClose().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } catch (DeploymentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if (ifValveAcknowledged){
                    return executor.submit(() -> {
                        return true;
                    });
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String timeAccepted = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                valveDataForMap.setTime_accepted(timeAccepted);
                valveDataForMap.setAlarm_description("Alarm accepted");
                valveDataForMap.setAcknowledged(true);

             updateValveAlarm(valveDataForMap);

            }//if (valveDataForMap.getSubType() == 100)

        } else {
          /*  return executor.submit(() -> {
                return false;
            });*/
        } // else
        return executor.submit(() -> {
            return true;
        });
    } // Make Valve alarm Acknowledged *************** Make Valve alarm Acknowledged


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

} // Class ValvesMasterService
