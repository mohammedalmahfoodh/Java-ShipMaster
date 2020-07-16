package com.kockumation.backEnd.levelMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kockumation.backEnd.levelMaster.model.*;

import javax.websocket.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ClientEndpoint
public class TankLiveDataSubscription {

    private Session session;
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();
   public static  TankSubscriptionData tankSubscriptionData;
   public DetectAndSaveAlarms detectAndSaveAlarms;
    public TankLiveDataSubscription() {
        detectAndSaveAlarms = new DetectAndSaveAlarms();
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        detectAndSaveAlarms = new DetectAndSaveAlarms();
        //boolean startNewThread
        detectAndSaveAlarms.start();

      //  timer.schedule(new DetectAndSaveAlarms(),0,2000);
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

        LavelMasterManager.IfTankLiveDataSubscription = false;
        return executor.submit(() -> {
            detectAndSaveAlarms.interrupt();
            DetectAndSaveAlarms.firstRun =true;
            DetectAndSaveAlarms.timer.cancel();
            DetectAndSaveAlarms.timer.purge();
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
