package com.kockumation.backEnd.ValvesMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kockumation.backEnd.ValvesMaster.model.AllValvesSetup;
import com.kockumation.backEnd.ValvesMaster.model.ValveDataForMap;
import com.kockumation.backEnd.ValvesMaster.model.ValveSetting;

import com.kockumation.backEnd.global.Db;
import com.kockumation.backEnd.utilities.GetValvesNames;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;
import com.kockumation.backEnd.utilities.ValveIdAndName;
import com.kockumation.backEnd.utilities.ValvesNames;

import javax.websocket.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ClientEndpoint
public class AllValvesSetupData {

    GetValvesNames getValvesNames;
    private AllValvesSetup allValvesSetup;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Session session;
    ValvesNames valvesNames;


    public AllValvesSetupData() {
        getValvesNames = new GetValvesNames();
        valvesNames = new ValvesNames();
    }

    // Create map valves with names *****************************************
    public Future<Boolean> createMapValvesWithNames() {
        System.out.println("Fetching valves names and ids....");
        try {
            List<ValveIdAndName> valveIdAndNameList = getValvesNames.getListOfValvesNames().getValvesNames();
            for (ValveIdAndName valveIdAndName : valveIdAndNameList) {
                //   System.out.println(valveIdAndName);
                ValveDataForMap valveDataForMap = new ValveDataForMap();
                valveDataForMap.setValve_id(valveIdAndName.getId());
                String name = valveIdAndName.getValveName().isEmpty() ? null : valveIdAndName.getValveName();
                valveDataForMap.setValve_name(name);
                Db.valveMapData.put(valveIdAndName.getId(), valveDataForMap);

            }

        } catch (Exception e) {
            System.out.println("Error during fetching file names .... ");
            return executor.submit(() -> {
                // Thread.sleep(3300);
                return false;
            });
        }
        return executor.submit(() -> {

            return true;
        });

    } //Create map valves with names  ************************************

    // Populate  map valves with valves settings type,subtype,errorTimeout *****************************************
    public Future<Boolean> populateMapValvesWithSettings() {

        try {
            for (ValveSetting valveSetting : allValvesSetup.getSetSmAllValvesSetupData()) {
                if (valveSetting.getSubType() != 99) {
                    ValveDataForMap valveDataForMap = Db.valveMapData.get(valveSetting.getId());
                    valveDataForMap.setValve_type(valveSetting.getType());
                    valveDataForMap.setSubType(valveSetting.getSubType());
                    valveDataForMap.setValve_type(valveSetting.getType());
                    valveDataForMap.setErrorTimeout(valveSetting.getErrorTimeout());
                    Db.valveMapData.put(valveSetting.getId(), valveDataForMap);
                }

            }

        } catch (NullPointerException e) {
            System.out.print("All valves setup not exists connect to web socket server..");
            return executor.submit(() -> {

                return false;
            });
        }

        return executor.submit(() -> {


            return true;
        });

    } //Populate  map valves with valves settings type,subtype,errorTimeout *****************************************

    // Check if data exists in Valves table *****************************************
    public Future<Boolean> checkIfDataExists() {
        String sql = "SELECT *  FROM valves WHERE valve_id=1 ";
        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                //    System.out.println(rs.getString(2));
                return executor.submit(() -> {
                    //  Thread.sleep(2000);
                    return true;
                });
            } else {
                //   System.out.println(rs);
                return executor.submit(() -> {
                    //   Thread.sleep(2000);
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

    }// Check if data exists in Valves table *****************************************


    // Insert Valves settings Into Valves table *****************************************
    public Future<Boolean> insertValvesSettings() {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            try {
                String query = "  INSERT INTO valves (valve_id,valve_name,valve_type,valve_subType,errorTimeout) VALUES (?,?,?,?,?);";
                PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                Db.valveMapData.entrySet().stream().forEach(e -> {
                    try {
                        preparedStmt.setInt(1, e.getValue().getValve_id());
                        preparedStmt.setString(2, e.getValue().getValve_name());
                        preparedStmt.setInt(3, e.getValue().getValve_type());
                        preparedStmt.setInt(4, e.getValue().getSubType());
                        preparedStmt.setInt(5, e.getValue().getErrorTimeout());
                        int rowAffected = preparedStmt.executeUpdate();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                });

            } catch (NullPointerException e) {
                System.out.print("All valves setup not exists connect to web socket server..");
                return executor.submit(() -> {

                    return false;
                });
            }
        } catch (SQLException ex) {
            return executor.submit(() -> {
                System.out.println("All valves settings inserted into valves table");
                return false;
            });

        }
        return executor.submit(() -> {
            ValvesMasterManager.ifAllValvesSetupDataInserted = true;
            return true;
        });

    }// Insert Valves settings Into Valves table *****************************************

    // Update All Valves setup data In Valve table *****************************************
    public Future<Boolean> updateValveSetup() {

        try (Connection conn = MySQLJDBCUtil.getConnection()) {

            try {
                String updateTanks = "UPDATE valves set valve_name = ?,valve_type = ?,valve_subType = ?,errorTimeout = ? where valve_id = ?;";
                PreparedStatement preparedStmt = conn.prepareStatement(updateTanks, Statement.RETURN_GENERATED_KEYS);

                Db.valveMapData.entrySet().stream().forEach(e -> {
                    try {
                        preparedStmt.setString(1, e.getValue().getValve_name());
                        preparedStmt.setInt(2, e.getValue().getValve_type());
                        preparedStmt.setInt(3, e.getValue().getSubType());
                        preparedStmt.setInt(4, e.getValue().getErrorTimeout());
                        preparedStmt.setInt(5, e.getValue().getValve_id());
                        int rowAffected = preparedStmt.executeUpdate();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });

            } catch (NullPointerException e) {
                System.out.print("All valves setup not exists connect to web socket server..");
                return executor.submit(() -> {

                    return false;
                });
            }

            return executor.submit(() -> {
                ValvesMasterManager.ifAllValvesSetupDataInserted = true;
                System.out.println("All valves settings data updated");
                return true;
            });
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return executor.submit(() -> {

                return false;
            });
        }

    }// Update All Valves setup data In Valve table *****************************************


    // Check if data exists in Tanks table *****************************************
    public Future<Boolean> getValvesSettings() {
        String sql = "SELECT *  FROM valves ";

        try (Connection conn = MySQLJDBCUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            //    System.out.println(rs.getString(2));
            while (rs.next()) {
                ValveDataForMap valveDataForMap = new ValveDataForMap();
                int valve_id = rs.getInt("valve_id");
                valveDataForMap.setValve_id(valve_id);
                valveDataForMap.setValve_type(rs.getInt("valve_type"));
                valveDataForMap.setSubType(rs.getInt("valve_subtype"));
                valveDataForMap.setErrorTimeout(rs.getInt("errorTimeout"));
                valveDataForMap.setValve_name(getValvesNames.getListOfValvesNames().getValvesNames().get(valve_id - 1).getValveName());
                Db.valveMapData.put(valve_id, valveDataForMap);
            }

        } catch (SQLException throwables) {
            return executor.submit(() -> {
                Thread.sleep(1000);
                return false;
            });
        }
        return executor.submit(() -> {
            Thread.sleep(1000);
            return true;
        });
    } // Get data from valves table *****************************************

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Valves settings WebSocket server connected.");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(message);
            if (node.has("setSmAllValvesSetupData")) {
                Gson gson = new Gson();
                allValvesSetup = gson.fromJson(message, AllValvesSetup.class);
                //   System.out.println(node);
                closeSession();

            } else {
                //  System.out.println(node);
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
    public void onClose() throws IOException {


    }

    public Future<Boolean> isClosed() throws IOException {

        return executor.submit(() -> {
            while (session.isOpen()) {

            }
            if (session.isOpen()) {
                System.out.println("Session still open");

                return false;
            } else {
                System.out.println("All valves settings data web Socket Session closed.");
                return true;
            }

        });

    }

    public void closeSession() {
        try {
            session.close();
        } catch (IOException ex) {
        }

    }// Close Session ()


}
