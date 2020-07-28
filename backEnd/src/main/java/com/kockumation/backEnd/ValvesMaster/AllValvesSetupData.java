package com.kockumation.backEnd.ValvesMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kockumation.backEnd.ValvesMaster.model.AllValvesSetup;
import com.kockumation.backEnd.ValvesMaster.model.ValveDataForMap;
import com.kockumation.backEnd.ValvesMaster.model.ValveSetting;
import com.kockumation.backEnd.levelMaster.LevelMasterManager;
import com.kockumation.backEnd.levelMaster.model.KslTankData;
import com.kockumation.backEnd.levelMaster.model.KslTanksData;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;
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
    private ExecutorService executor;
    private Session session;
    ValvesNames valvesNames;

    public AllValvesSetup getAllValvesSetup() {
        return allValvesSetup;
    }

    public void setAllValvesSetup(AllValvesSetup allValvesSetup) {
        this.allValvesSetup = allValvesSetup;
    }

    public AllValvesSetupData() {
        getValvesNames = new GetValvesNames();
        allValvesSetup = new AllValvesSetup();
        executor = Executors.newSingleThreadExecutor();
        valvesNames = new ValvesNames();
    }

    // Create map valves with names *****************************************
    public Future<Boolean> createMapValvesWithNames() {
        List<ValveIdAndName> valveIdAndNameList = getValvesNames.getListOfValvesNames().getValvesNames();
        for (ValveIdAndName valveIdAndName : valveIdAndNameList) {
            //   System.out.println(valveIdAndName);
            ValveDataForMap valveDataForMap = new ValveDataForMap();
            valveDataForMap.setValve_id(valveIdAndName.getId());
            valveDataForMap.setValve_name(valveIdAndName.getValveName());
            ValvesMasterManager.valveMapData.put(valveIdAndName.getId(), valveDataForMap);

        }
        return executor.submit(() -> {

            return true;
        });

    } //Create map valves with names  ************************************

    // Populate  map valves with valves settings type,subtype,errorTimeout *****************************************
    public Future<Boolean> populateMapValvesWithSettings() {

        try {
            for (ValveSetting valveSetting : allValvesSetup.getSetSmAllValvesSetupData()) {

                ValveDataForMap valveDataForMap = ValvesMasterManager.valveMapData.get(valveSetting.getId());
                //   System.out.println(valveSetting);
                valveDataForMap.setSubType(valveSetting.getSubType());
                valveDataForMap.setValve_type(valveSetting.getType());
                valveDataForMap.setErrorTimeout(valveSetting.getErrorTimeout());
                //  ValvesMasterManager.valveMapData.put(valveSetting.getId(),valveDataForMap);
            }

        } catch (NullPointerException e) {
            System.out.print("All valves setup not exists connect to websocket server..");
            return executor.submit(() -> {

                return false;
            });
        }

        return executor.submit(() -> {

            System.out.println("Valves setup data populated now..");
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

                for (ValveSetting valveSetting : allValvesSetup.getSetSmAllValvesSetupData()) {

                    int valve_id = valveSetting.getId();
                    preparedStmt.setInt(1, valve_id);
                    preparedStmt.setString(2, valveSetting.getValve_name());
                    preparedStmt.setInt(3, valveSetting.getType());
                    preparedStmt.setInt(4, valveSetting.getSubType());
                    preparedStmt.setInt(5, valveSetting.getErrorTimeout());
                    int rowAffected = preparedStmt.executeUpdate();

                }
            } catch (NullPointerException e) {
                System.out.print("All valves setup not exists connect to websocket server..");
                return executor.submit(() -> {

                    return false;
                });
            }
        } catch (SQLException ex) {
            return executor.submit(() -> {
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

                for (ValveSetting valveSetting : allValvesSetup.getSetSmAllValvesSetupData()) {
                    int valve_id = valveSetting.getId();

                    preparedStmt.setString(1, valveSetting.getValve_name());
                    preparedStmt.setInt(2, valveSetting.getType());
                    preparedStmt.setInt(3, valveSetting.getSubType());
                    preparedStmt.setInt(4, valveSetting.getErrorTimeout());
                    preparedStmt.setInt(5, valve_id);

                    int rowAffected = preparedStmt.executeUpdate();

                }

            } catch (NullPointerException e) {
                System.out.print("All valves setup not exists connect to websocket server..");
                return executor.submit(() -> {

                    return false;
                });
            }

            return executor.submit(() -> {
                ValvesMasterManager.ifAllValvesSetupDataInserted = true;

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
                ValvesMasterManager.valveMapData.put(valve_id, valveDataForMap);
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

                populateMapValvesWithSettings();
                closeSession();
                //  closeSession();
                //  session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Game ended"));
            }else {
                System.out.println(node);
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

        System.out.println("All valves settings data websocket Session closed.");

        return executor.submit(() -> {
            //  insertValvesSettings();
            return true;
        });

    }

    public void closeSession() {
        try {
            session.close();
        } catch (IOException ex) {
        }

    }// Close Session ()


}
