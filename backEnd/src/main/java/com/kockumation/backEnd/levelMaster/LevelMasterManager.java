package com.kockumation.backEnd.levelMaster;

import com.kockumation.backEnd.global.Db;
import com.kockumation.backEnd.global.GlobalVariableSingleton;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;
import org.json.simple.JSONObject;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LevelMasterManager extends Thread {
    //  private final String uri = "ws://localhost:8089";
  //  private final String uri = "ws://192.168.190.232:8089";
      private final String  uri = GlobalVariableSingleton.getInstance().getLocaluri();


    AllTanksDataFromKsl allTanksDataFromKsl;
    TankSettingsData tankSettingsData;
    LiveDataWebsocketClient liveDataWebsocketClient;
    boolean kslDataInserted = false;





    public LevelMasterManager() {

        allTanksDataFromKsl = new AllTanksDataFromKsl();
        tankSettingsData = new TankSettingsData();
        liveDataWebsocketClient = new LiveDataWebsocketClient();
    }

    public boolean checkIfInserted() {

        return kslDataInserted;
    }

    @Override
    public void run() {
        levelMasterEngine();
    }

    public void levelMasterEngine() {

        boolean ifWebsocketReady = false;
        boolean ifTankSettingsWebsocketReady = false;

        JSONObject getKslTankData = new JSONObject();
        JSONObject vessel2 = new JSONObject();
        vessel2.put("vessel", 1);
        getKslTankData.put("getKslTankData", vessel2);
        String getKslTankDataStr = getKslTankData.toString();


        while (!ifWebsocketReady) {
            try {

                GlobalVariableSingleton.getInstance().getClient().connectToServer(allTanksDataFromKsl, new URI(uri));
                allTanksDataFromKsl.sendMessage(getKslTankDataStr);

                ifWebsocketReady = true;
                boolean checkIfDataExistsInDB = false;
                checkIfDataExistsInDB = allTanksDataFromKsl.checkIfDataExists().get();

                if (checkIfDataExistsInDB) {
                    System.out.println("Data exists in db");
                    allTanksDataFromKsl.updateAllKslDataInTanks();
                    kslDataInserted = true;
                } else {
                    System.out.println("No Data exists in db");
                    kslDataInserted = allTanksDataFromKsl.insertAllKslDataIntoTanks(allTanksDataFromKsl.getKslTanksData().getSetKslTankData()).get();
                    if (kslDataInserted) {
                        System.out.println("Tanks info Inserted into database");
                    } else {
                        System.out.println("Tanks info not Inserted into database");
                    }

                }

                allTanksDataFromKsl = null;

            } catch (DeploymentException e) {
                System.out.println("Web socket not ready start web socket server.");
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
        while (!ifTankSettingsWebsocketReady && kslDataInserted) {
            try {
                GlobalVariableSingleton.getInstance().getClient().connectToServer(tankSettingsData, new URI(uri));
                ifTankSettingsWebsocketReady = true;

                for (Map.Entry<Integer, TankDataForMap> entry : Db.tankMapData.entrySet()) {
                    JSONObject getTankSetting = new JSONObject();
                    JSONObject tankId = new JSONObject();
                    tankId.put("tankId", entry.getKey());
                    getTankSetting.put("getTankSettingsData", tankId);
                    String getTankSettingStr = getTankSetting.toString();
                    tankSettingsData.sendMessage(getTankSettingStr);
                    //    System.out.println("Key = " + entry.getKey() +
                    //           ", Value = " + entry.getValue());
                }
               boolean WebSocketSettingsClosed = false;
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
        try {

            if (kslDataInserted) {
                Thread.sleep(1000);
                JSONObject tankSubscription = new JSONObject();
                JSONObject tankId = new JSONObject();
                tankId.put("tankId", 0);
                tankSubscription.put("setTankSubscriptionOn", tankId);
                String tankSubscriptionStr = tankSubscription.toString();
                GlobalVariableSingleton.getInstance().getClient().connectToServer(liveDataWebsocketClient, new URI(uri));
                liveDataWebsocketClient.sendMessage(tankSubscriptionStr);
            }

        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (DeploymentException e) {
            System.out.println("Live Data Web socket not ready start web socket server.");

            DetectAndSaveAlarms.timer.cancel();

            //  e.printStackTrace();
        } catch (IOException e) {
            //   e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // Subscribe to tanks live data   ************** Subscribe to tanks live data **********************


    } // LevelMaster engine



}
