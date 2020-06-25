package com.kockumation.backEnd.levelMaster;

import com.kockumation.backEnd.levelMaster.model.KslTankData;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;
import org.glassfish.tyrus.client.ClientManager;
import org.json.simple.JSONObject;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LavelMasterManager {
    //  private final String uri = "ws://localhost:8089";
     private final String uri="ws://192.168.190.232:8089";

    AllTanksDataFromKsl allTanksDataFromKsl;
    TankSettingsData tankSettingsData;
    TankLiveDataSubscription tankLiveDataSubscription;
    public static Map<Integer, TankDataForMap> tankMapData = new HashMap<Integer, TankDataForMap>();
    public static boolean kslDataInserted = false;
    public static boolean kslWebSocketClosed = false;
    public static boolean WebSocketSettingsClosed = false;
    public static boolean checkIfDataExistsInDB = false;
    public static boolean IfTankLiveDataSubscription = false;
    public LavelMasterManager() {
        allTanksDataFromKsl = new AllTanksDataFromKsl();
        tankSettingsData = new TankSettingsData();
        tankLiveDataSubscription = new TankLiveDataSubscription();
    }

    public boolean checkIfInserted() {

        return kslDataInserted;
    }

    public void levelMasterEngine() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now(Clock.systemUTC()).format(formatter);
        System.out.println(now);

        boolean ifWebsocketReady = false;
        boolean ifTankSettingsWebsocketReady = false;

        ClientManager client = ClientManager.createClient();
        JSONObject getKslTankData = new JSONObject();
        JSONObject vessel2 = new JSONObject();
        vessel2.put("vessel", 1);
        getKslTankData.put("getKslTankData", vessel2);
        String getKslTankDataStr = getKslTankData.toString();


        while (!ifWebsocketReady) {
            try {

                client.connectToServer(allTanksDataFromKsl, new URI(uri));
                allTanksDataFromKsl.sendMessage(getKslTankDataStr);
                ifWebsocketReady = true;
                kslWebSocketClosed = allTanksDataFromKsl.onClose().get();
                System.out.println("now closed inside manager");
                checkIfDataExistsInDB = allTanksDataFromKsl.checkIfDataExists().get();
                if (checkIfDataExistsInDB) {
                    System.out.println("Data exists in db");
                    allTanksDataFromKsl.updateAllKslDataInTanks();
                } else {
                    System.out.println("No Data exists in db");
                    kslDataInserted = allTanksDataFromKsl.insertAllKslDataIntoTanks(allTanksDataFromKsl.getKslTanksData().getSetKslTankData()).get();
                    System.out.println("now Inserted into database");
                }

                allTanksDataFromKsl = null;


            } catch (DeploymentException e) {
                System.out.println("Websocket not ready start websocket server.");
                ifWebsocketReady = false;
                allTanksDataFromKsl = null;
                //   e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }// While

        // Get tanks settings     *******************************************************
        while (!ifTankSettingsWebsocketReady) {
            try {
                client.connectToServer(tankSettingsData, new URI(uri));
                ifTankSettingsWebsocketReady = true;

                for (Map.Entry<Integer, TankDataForMap> entry : tankMapData.entrySet()) {
                    JSONObject getTankSetting = new JSONObject();
                    JSONObject tankId = new JSONObject();
                    tankId.put("tankId", entry.getKey());
                    getTankSetting.put("getTankSettingsData", tankId);
                    String getTankSettingStr = getTankSetting.toString();
                    tankSettingsData.sendMessage(getTankSettingStr);
                    //    System.out.println("Key = " + entry.getKey() +
                    //           ", Value = " + entry.getValue());
                }

                try {
                    WebSocketSettingsClosed = tankSettingsData.onClose().get();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (WebSocketSettingsClosed) {
                    System.out.println("WebSocket Settings Closed Now.");
                    tankSettingsData.updateAllTanksSettings().get();

                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (DeploymentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


        }  // Get tanks settings     *******************************************************

        // Subscribe to tanks live data   ************** Subscribe to tanks live data **********************
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {

               if (!IfTankLiveDataSubscription){
                   Thread.sleep(1000);
                   JSONObject tankSubscription = new JSONObject();
                   JSONObject tankId = new JSONObject();
                   tankId.put("tankId",0);
                   tankSubscription.put("setTankSubscriptionOn", tankId);
                   String tankSubscriptionStr = tankSubscription.toString();
                   client.connectToServer(tankLiveDataSubscription, new URI(uri));
                   tankLiveDataSubscription.sendMessage(tankSubscriptionStr);
                   IfTankLiveDataSubscription =true;
               }



            } catch (InterruptedException e) {
                //e.printStackTrace();
            } catch (DeploymentException e) {
                System.out.println("Live Data Websocket not ready start websocket server.");
                IfTankLiveDataSubscription =false;
              //  e.printStackTrace();
            } catch (IOException e) {
             //   e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        // Subscribe to tanks live data   ************** Subscribe to tanks live data **********************


    } // LevelMaster engine

    public void printMapData() {
        for (Map.Entry<Integer, TankDataForMap> entry : LavelMasterManager.tankMapData.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }


}
