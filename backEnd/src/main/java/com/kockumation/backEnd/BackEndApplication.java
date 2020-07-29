package com.kockumation.backEnd;

import com.kockumation.backEnd.ValvesMaster.DetectAndSaveValvesAlarms;
import com.kockumation.backEnd.ValvesMaster.ValvesMasterManager;
import com.kockumation.backEnd.global.GlobalVariableSingleton;
import com.kockumation.backEnd.levelMaster.LevelMasterManager;
import com.kockumation.backEnd.services.WebSocketClient;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BackEndApplication {

    public static void main(String[] args) throws IOException, URISyntaxException, DeploymentException {
        SpringApplication.run(BackEndApplication.class, args);
        System.out.println("listening on port " + 8081);


        boolean checkDataBase = true;
        while (checkDataBase) {

            try (Connection conn = MySQLJDBCUtil.getConnection()) {
                checkDataBase = false;
                System.out.println(String.format("Connected to database %s " + "successfully.", conn.getCatalog()));

                WebSocketClient webSocketClient = new WebSocketClient();
                final String uri = "ws://192.168.190.232:8089";
                LevelMasterManager levelMasterManager;
                ValvesMasterManager valvesMasterManager;

                // Try connecting to web socket



                while (true) {
                    try {
                        if (webSocketClient.session == null){
                            GlobalVariableSingleton.getInstance().getClient().connectToServer(webSocketClient, new URI(uri));
                            System.out.println("web socket is Opened");
                        }

                    } catch (DeploymentException e) {

                        System.out.println("Live Data Websocket not ready start websocket server.");
                    }

                  

                    // valvesMasterManager.start();
                    //levelMasterManager.start();


                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            } catch (SQLException ex) {
                //System.out.println(ex.getMessage());
                switch (ex.getErrorCode()) {
                    case 1049:
                        System.out.println("Please install database ship_master_java.");
                        break;
                    case 1045:
                        System.out.println("Please change database user to root and password to tyfon");
                        break;
                }
                try {
                    Thread.sleep(3000);
                    checkDataBase = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }





	/*JSONObject setTankSubscriptionOn = new JSONObject();
		JSONObject tankId = new JSONObject();
		tankId.put( "tankId",0);
		setTankSubscriptionOn.put("setTankSubscriptionOn", tankId);
		String setTankSubscriptionOnStr = setTankSubscriptionOn.toString();

	//	allTanksDataFromKsl.sendMessage(setTankSubscriptionOnStr);*/


    }


}
