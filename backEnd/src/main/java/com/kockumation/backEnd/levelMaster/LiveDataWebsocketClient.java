package com.kockumation.backEnd.levelMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kockumation.backEnd.ValvesMaster.DetectAndSaveValvesAlarms;
import com.kockumation.backEnd.ValvesMaster.ValvesMasterManager;
import com.kockumation.backEnd.ValvesMaster.model.ValvesSubscriptionData;
import com.kockumation.backEnd.levelMaster.model.*;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ClientEndpoint
public class LiveDataWebsocketClient {

    private Session session;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();
    public static TankSubscriptionData tankSubscriptionData;
    public static ValvesSubscriptionData valvesSubscriptionData;
    public DetectAndSaveAlarms detectAndSaveAlarms;
    public DetectAndSaveValvesAlarms detectAndSaveValvesAlarms;

    public LiveDataWebsocketClient() {
        detectAndSaveAlarms = new DetectAndSaveAlarms();
        detectAndSaveValvesAlarms = new DetectAndSaveValvesAlarms();
    }


    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
          detectAndSaveValvesAlarms.createNewTimer();
        detectAndSaveAlarms.createNewTimer();
        System.out.println("Live data web socket opened....");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(message);
            if (node.has("setSmValveSubscriptionData")) {
                //   System.out.println("Valves live data");
                Gson gson = new Gson();
                valvesSubscriptionData = gson.fromJson(message, ValvesSubscriptionData.class);
                //  System.out.println(node);

            }

            if (node.has("setTankSubscriptionData")) {

                //  System.out.println(node);
                Gson gson = new Gson();
                tankSubscriptionData = gson.fromJson(message, TankSubscriptionData.class);


                //   System.out.println(tankSubscriptionData.getSetTankSubscriptionData());

            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    } // OnMessage

    public void sendMessage(String message) {

        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {

        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
        System.out.println("Error occurred.");
    }

    @OnClose
    public Future<Boolean> onClose() throws IOException {

        DetectAndSaveAlarms.timer.cancel();
//        DetectAndSaveValvesAlarms.timer.cancel();
        return executor.submit(() -> {
            System.out.println("Live Data WebSocket closed ");
            return true;
        });
    }

    public void closeSession() {
        try {

            session.close();
        } catch (IOException ex) {

        }
    }


}//Class
