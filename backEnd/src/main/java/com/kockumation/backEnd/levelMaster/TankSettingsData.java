package com.kockumation.backEnd.levelMaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kockumation.backEnd.levelMaster.model.KslTanksData;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class TankSettingsData {
    //  private final String uri="ws://192.168.190.232:8089";
    private final String uri = "ws://localhost:8089";
    private Session session;
    private WebSocketContainer container;

    public TankSettingsData() {
        try {
            container = ContainerProvider.
                    getWebSocketContainer();
            container.connectToServer(this, new URI(uri));

        } catch (Exception ex) {
            System.out.println("Websocket not ready start websocket server");
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        //  System.out.println("Connection opened.");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws InterruptedException {
        //  System.out.println(session.getId());
        //  System.out.println(message);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(message);
            if (node.has("setKslTankData")) {
                System.out.println("Ok");
                Gson gson = new Gson();
                KslTanksData kslTanksData = gson.fromJson(message, KslTanksData.class);


                  //  updateAllKslDataInTanks(kslTanksData.getSetKslTankData());



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
