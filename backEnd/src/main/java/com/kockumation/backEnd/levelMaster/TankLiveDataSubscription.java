package com.kockumation.backEnd.levelMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kockumation.backEnd.levelMaster.model.*;

import javax.websocket.*;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ClientEndpoint
public class TankLiveDataSubscription {
    //  private final String uri="ws://192.168.190.232:8089";
    private final String uri = "ws://localhost:8089";
    private Session session;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();
   public static  TankSubscriptionData tankSubscriptionData;
    Timer timer = new Timer();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        //  System.out.println("Connection opened.");
        timer.schedule(new DetectAndSaveAlarms(),0,9000);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(message);
            if (node.has("setTankSubscriptionData")) {

                Gson gson = new Gson();
                 tankSubscriptionData = gson.fromJson(message, TankSubscriptionData.class);
                System.out.println(tankSubscriptionData.getSetTankSubscriptionData());

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

        LavelMasterManager.IfTankLiveDataSubscription = false;
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
