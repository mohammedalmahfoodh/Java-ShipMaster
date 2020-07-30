package com.kockumation.backEnd.levelMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kockumation.backEnd.levelMaster.model.*;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;

import javax.websocket.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ClientEndpoint
public class TankSettingsData {
    //  private final String uri="ws://192.168.190.232:8089";
    private final String uri = "ws://localhost:8089";
    private Session session;
    private WebSocketContainer container;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();


    // Update Tanks settings   ********************** Update Tanks settings *********************************
    public Future<Boolean> updateAllTanksSettings() {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            String updateTanks = "UPDATE tanks SET low_alarm_limit = ?,low_low_alarm_limit = ?,high_alarm_limit =?,high_high_alarm_limit =?,max_volume=? where tank_id = ?;";
            PreparedStatement preparedStmt = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
            for (Map.Entry<Integer, TankDataForMap> entry : LevelMasterManager.tankMapData.entrySet()) {
              //  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().getTankHighLevel());
                TankDataForMap tankDataForMap = LevelMasterManager.tankMapData.get(entry.getKey());

                if (tankDataForMap.getTankHighLevel() > tankDataForMap.getHighHighLevel()) {

                    float tankHighLevelTemp = tankDataForMap.getTankHighLevel();
                    tankDataForMap.setTankHighLevel(tankDataForMap.getHighHighLevel());
                    ;
                    tankDataForMap.setHighHighLevel(tankHighLevelTemp);

                }
                if ((tankDataForMap.getTankLowLevel() < tankDataForMap.getTankLowLowLevel()) && (tankDataForMap.getTankLowLevel() != -10)) {
                    float tankLowLevelTemp = tankDataForMap.getTankLowLevel();
                    tankDataForMap.setTankLowLevel(tankDataForMap.getTankLowLowLevel());
                    tankDataForMap.setTankLowLowLevel(tankLowLevelTemp);
                }

                preparedStmt.setFloat(1, tankDataForMap.getTankLowLevel());
                preparedStmt.setFloat(2, tankDataForMap.getTankLowLowLevel());
                preparedStmt.setFloat(3, tankDataForMap.getTankHighLevel());
                preparedStmt.setFloat(4, tankDataForMap.getHighHighLevel());
                preparedStmt.setFloat(5, tankDataForMap.getHighHighLevel());
                preparedStmt.setInt(6, tankDataForMap.getTank_id());
                int rowAffected = preparedStmt.executeUpdate();

                if (tankDataForMap.getTank_id() == LevelMasterManager.tankMapData.size()){
                    return executor.submit(() -> {
                        System.out.println("Tanks table with High,HighHigh,Low,LowLow and max volume values has been updated.");

                        return true;
                    });
                }
            }


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
                // Thread.sleep(3000);
                return false;
            });
        }
        return executor.submit(() -> {
            System.out.println("Tanks table updated High and max volume");
            //  Thread.sleep(3000);
            return false;
        });

    } //Update Tanks settings   ********************** Update Tanks settings **********************************


    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        //  System.out.println("Connection opened.");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {
        //  System.out.println(session.getId());
        //  System.out.println(message);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(message);
            if (node.has("setTankSettingsData")) {

                Gson gson = new Gson();
                SetTankSettings setTankSettings = gson.fromJson(message, SetTankSettings.class);
                TankSettingData tankSettingData = setTankSettings.getSetTankSettingsData();
                TankDataForMap tankDataForMap = LevelMasterManager.tankMapData.get(tankSettingData.getTankId());

                tankDataForMap.setMax_volume(tankSettingData.getMaxVolume());
                tankDataForMap.setTankHighLevel(tankSettingData.getHighLevel());
                tankDataForMap.setHighHighLevel(tankSettingData.getHighHighLevel());
                tankDataForMap.setTankLowLevel(tankSettingData.getLowLevel());
                tankDataForMap.setTankLowLowLevel(tankSettingData.getLowLowLevel());


                if (setTankSettings.getSetTankSettingsData().getTankId() == LevelMasterManager.tankMapData.size()) {

                    closeSession();
                }

            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }

    public void sendMessage(String message) {

        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {

        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public Future<Boolean> onClose() throws IOException {
        //prepare the endpoint for closing.
        container = null;

        return executor.submit(() -> {
            Thread.sleep(1000);
            System.out.println("WebSocket settings  closed ");
            return true;
        });
    }

    public void closeSession() {
        try {

            session.close();
        } catch (IOException ex) {

        }
    }

}
