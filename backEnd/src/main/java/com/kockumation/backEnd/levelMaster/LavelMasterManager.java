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

public class LavelMasterManager {
    private final String uri="ws://localhost:8089";
   // private final String uri="ws://192.168.190.232:8089";

   AllTanksDataFromKsl allTanksDataFromKsl;
    public static  HashMap<Integer, TankDataForMap> tankMapData=new HashMap<Integer,TankDataForMap>();
    public static  boolean kslDataInserted = false;
    public LavelMasterManager() {
        allTanksDataFromKsl = new AllTanksDataFromKsl();
    }
    public boolean checkIfInserted(){

        return kslDataInserted;
    }

    public void levelMasterEngine() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String now = LocalDateTime.now(Clock.systemUTC()).format(formatter);
        System.out.println(now);

        boolean ifWebsocketReady = false;


        ClientManager client = ClientManager.createClient();
        JSONObject getKslTankData = new JSONObject();
        JSONObject vessel2 = new JSONObject();
        vessel2.put("vessel", 1);
        getKslTankData.put("getKslTankData", vessel2);
        String getKslTankDataStr = getKslTankData.toString();



        while (!ifWebsocketReady ) {
            try {
                AllTanksDataFromKsl allTanksDataFromKsl = new AllTanksDataFromKsl();
                client.connectToServer(allTanksDataFromKsl, new URI(uri));
                allTanksDataFromKsl.sendMessage(getKslTankDataStr);
                ifWebsocketReady = true;
                allTanksDataFromKsl = null;



            } catch (DeploymentException e) {
                System.out.println("Websocket not ready start websocket server.");
                ifWebsocketReady = false;
                allTanksDataFromKsl = null;
                //   e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }// While

      // Get tanks settings     *******************************************************
        try {
            Thread.sleep(2000);
            System.out.println(tankMapData.size());
            TankSettingsData tankSettingsData = new TankSettingsData();
            client.connectToServer(tankSettingsData, new URI(uri));
            






        } catch (InterruptedException | URISyntaxException | DeploymentException | IOException e) {
            e.printStackTrace();
        }
        // Get tanks settings     *******************************************************

    } // LevelMaster engine







}
