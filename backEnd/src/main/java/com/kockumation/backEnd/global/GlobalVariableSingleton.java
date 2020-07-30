package com.kockumation.backEnd.global;

import org.glassfish.tyrus.client.ClientManager;

public class GlobalVariableSingleton {


    private static final GlobalVariableSingleton globalVariableSingleton = new GlobalVariableSingleton();



    private  ClientManager client = ClientManager.createClient();


    private GlobalVariableSingleton() {

    }

    public ClientManager getClient(){

        return client;
    }

    //this is the method to obtain the single instance
    public static GlobalVariableSingleton getInstance() {
        return globalVariableSingleton;
    }


}
