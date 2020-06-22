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
    public AllTanksDataFromKsl() {

        try {
            container = ContainerProvider.
                    getWebSocketContainer();
            container.connectToServer(this, new URI(uri));

        } catch (Exception ex) {
            System.out.println("Websocket not ready start websocket server");
        }
    }

    // Check if data exists in Tanks table *****************************************
    public Boolean checkIfDataExists() {
        String sql = "SELECT *  FROM tanks WHERE tank_id=1 ";
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                //    System.out.println(rs.getString(2));
                return true;
            } else {
                //   System.out.println(rs);
                return false;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
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

            return true;
        });

    }// Insert KslTanksData Into Tanks table *****************************************

    // Update All KslTanksData In Tanks table *****************************************
    public boolean updateAllKslDataInTanks(List<KslTankData> listOfKslTanksData) {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            String updateTanks = "UPDATE tanks set code_name = ?,volume = ?,density =? where tank_id = ?;";

            PreparedStatement preparedStmt = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);
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
                preparedStmt.setInt(4, index);
                int rowAffected = preparedStmt.executeUpdate();
               // System.out.println(rowAffected);
               // System.out.println(index);
                if (listOfKslTanksData.size() == index){
                    LavelMasterManager.kslDataInserted = true;
                    //System.out.println(LavelMasterManager.kslDataInserted);
                }
                index++;
            }

            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }

    }// Update All KslTanksData In Tanks table *****************************************


    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        //  System.out.println("Connection opened.");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {
        //  System.out.println(session.getId());
        //  System.out.println(message);
        boolean ifDataExists = checkIfDataExists();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(message);
            if (node.has("setKslTankData")) {
                System.out.println("Ok");
                Gson gson = new Gson();
                KslTanksData kslTanksData = gson.fromJson(message, KslTanksData.class);
                if (ifDataExists) {

                 //   updateAllKslDataInTanks(kslTanksData.getSetKslTankData());
                } else {
               //     insertAllKslDataIntoTanks(kslTanksData.getSetKslTankData());
                    System.out.println("No data");
                }

                closeSession();
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
    public void onClose(Session session, CloseReason reason) throws IOException {
        //prepare the endpoint for closing.
        container = null;
        System.out.println("Session closed.");

    }

    public void closeSession() {
        try {

            session.close();
        } catch (IOException ex) {

        }
    }

}
