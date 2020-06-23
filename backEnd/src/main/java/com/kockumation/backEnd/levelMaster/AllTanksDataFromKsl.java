package com.kockumation.backEnd.levelMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kockumation.backEnd.levelMaster.model.KslTankData;
import com.kockumation.backEnd.levelMaster.model.KslTanksData;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;
import org.json.simple.JSONObject;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

import java.sql.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@ClientEndpoint
public class AllTanksDataFromKsl {
    //  private final String uri="ws://192.168.190.232:8089";
    private final String uri = "ws://localhost:8089";
    private Session session;
    private WebSocketContainer container;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();
    private KslTanksData kslTanksData;

    public KslTanksData getKslTanksData() {
        return kslTanksData;
    }

    public void setKslTanksData(KslTanksData kslTanksData) {
        this.kslTanksData = kslTanksData;
    }


    // Check if data exists in Tanks table *****************************************
    public Future<Boolean> checkIfDataExists() {
        String sql = "SELECT *  FROM tanks WHERE tank_id=1 ";
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                //    System.out.println(rs.getString(2));
                return executor.submit(() -> {
                    Thread.sleep(2000);
                    return true;
                });
            } else {
                //   System.out.println(rs);
                return executor.submit(() -> {
                    Thread.sleep(2000);
                    return false;
                });
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
           //     Thread.sleep(2000);
                return false;
            });
        }

    }// Check if data exists in Tanks table *****************************************

    // Insert KslTanksData Into Tanks table *****************************************
    public Future<Boolean> insertAllKslDataIntoTanks(List<KslTankData> listOfKslTanksData) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            String query = "INSERT INTO tanks (code_name,volume,density) VALUES (?,?,?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (KslTankData kslTankData : listOfKslTanksData) {
                TankDataForMap tankDataForMap = new TankDataForMap();
                tankDataForMap.setTank_id(index);
                tankDataForMap.setCode_name(kslTankData.getTankCode());
                tankDataForMap.setVolume(kslTankData.getVolume());
                tankDataForMap.setDensity(kslTankData.getDensity());

                LavelMasterManager.tankMapData.put(index,tankDataForMap);

                preparedStmt.setString(1, kslTankData.getTankCode());
                preparedStmt.setFloat(2, kslTankData.getVolume());
                preparedStmt.setDouble(3, kslTankData.getDensity());
                int rowAffected = preparedStmt.executeUpdate();

                index++;

            }

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

    }// Insert KslTanksData Into Tanks table *****************************************

    // Update All KslTanksData In Tanks table *****************************************
    public Future<Boolean> updateAllKslDataInTanks() {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            String updateTanks = "UPDATE tanks set code_name = ?,volume = ?,density =? where tank_id = ?;";

            PreparedStatement preparedStmt = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (KslTankData kslTankData : kslTanksData.getSetKslTankData()) {
                TankDataForMap tankDataForMap = new TankDataForMap();
                tankDataForMap.setTank_id(index);
                tankDataForMap.setCode_name(kslTankData.getTankCode());
                tankDataForMap.setVolume(kslTankData.getVolume());
                tankDataForMap.setDensity(kslTankData.getDensity());
                LavelMasterManager.tankMapData.put(index,tankDataForMap);

                preparedStmt.setString(1, kslTankData.getTankCode());
                preparedStmt.setFloat(2, kslTankData.getVolume());
                preparedStmt.setDouble(3, kslTankData.getDensity());
                preparedStmt.setInt(4, index);
                int rowAffected = preparedStmt.executeUpdate();
               // System.out.println(rowAffected);
               // System.out.println(index);

                index++;
            }

            return executor.submit(() -> {
                System.out.println("Tanks table updated");
              //  Thread.sleep(3000);
                return true;
            });
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {
               // Thread.sleep(3000);
                return false;
            });
        }

    }// Update All KslTanksData In Tanks table *****************************************


    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        //  System.out.println("Connection opened.");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(message);
            if (node.has("setKslTankData")) {
                Gson gson = new Gson();
                 kslTanksData = gson.fromJson(message, KslTanksData.class);

                closeSession();
                //  session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Game ended"));
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
        System.out.println("KslTankData websocket Session closed.");
        return executor.submit(() -> {
            Thread.sleep(1000);
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
