package com.kockumation.backEnd;

import com.kockumation.backEnd.ValvesMaster.ValvesMasterManager;
import com.kockumation.backEnd.global.GlobalVariableSingleton;
import com.kockumation.backEnd.levelMaster.LevelMasterManager;
import com.kockumation.backEnd.global.Db;
import com.kockumation.backEnd.global.WebSocketClient;
import com.kockumation.backEnd.utilities.MySQLJDBCUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.websocket.DeploymentException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@SpringBootApplication
public class BackEndApplication {

    public static void main(String[] args) throws IOException, URISyntaxException, DeploymentException {
        SpringApplication.run(BackEndApplication.class, args);
        System.out.println("listening on port " + 3001);
        boolean checkDataBase = true;
        while (checkDataBase) {

            try (Connection conn = MySQLJDBCUtil.getConnection()) {
                checkDataBase = false;
                WebSocketClient webSocketClient = new WebSocketClient();

                String uriLocalIp = GlobalVariableSingleton.getInstance().getLocaluri();

                System.out.println(String.format("Connected to database %s " + "successfully.", conn.getCatalog()));

                LevelMasterManager levelMasterManager;
                ValvesMasterManager valvesMasterManager;

                // Try connecting to web socket
                while (true) {
                    try {
                        if (webSocketClient.session == null) {
                            GlobalVariableSingleton.getInstance().getClient().connectToServer(webSocketClient, new URI(uriLocalIp));
                            System.out.println("web Socket is Opened");
                            levelMasterManager = new LevelMasterManager();
                            levelMasterManager.start();

                              valvesMasterManager = new ValvesMasterManager();
                              valvesMasterManager.start();

                        }

                    } catch (DeploymentException e) {

                        levelMasterManager = null;
                        valvesMasterManager = null;
                            ValvesMasterManager.ifAllValvesSetupDataInserted = false;

                        Db.valveMapData.clear();
                        Db.tankMapData.clear();

                        System.out.println("Live Data Web Socket not ready start web Socket server.");
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } // While true


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
                    Thread.sleep(4000);
                    checkDataBase = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } // while (checkDataBase)


    }


}
