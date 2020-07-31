package com.kockumation.backEnd.global;

import org.glassfish.tyrus.client.ClientManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class GlobalVariableSingleton {


    private static final GlobalVariableSingleton globalVariableSingleton = new GlobalVariableSingleton();



    private  ClientManager client = ClientManager.createClient();


    private GlobalVariableSingleton() {

    }
    // Get local uri for web socket server from application.properties located in config folder  ************
    public String getLocaluri(){


        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream("c:\\Program Files (x86)\\Kockum Sonics\\ShipMaster-backEnd\\config\\application.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        prop = new Properties();
        try {
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String uriLocalIp = prop.getProperty("uriLocalIp");
        return uriLocalIp;
    } // Get local uri for web socket server from application.properties located in config folder  ************

    public ClientManager getClient(){

        return client;
    }

    //this is the method to obtain the single instance
    public static GlobalVariableSingleton getInstance() {
        return globalVariableSingleton;
    }


}
