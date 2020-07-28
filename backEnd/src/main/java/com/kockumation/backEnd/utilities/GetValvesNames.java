package com.kockumation.backEnd.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GetValvesNames {
    ValvesNames valvesNames = new ValvesNames();

    private String getValvesNamesString() {
        File file = null;

        file = new File("C:/Program Files (x86)/Kockum Sonics/ShipMaster-backEnd/config/ValvesNames.json");

        String content = null;
        try {
            content = new String(Files.readAllBytes(file.toPath()));
            //  System.out.println(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println(content);
        return content;
    }

    // Get list of Valves names ********** Get list of Valves names *************************
    public ValvesNames getListOfValvesNames() {

        try {
            valvesNames = new ObjectMapper().readValue(getValvesNamesString(), ValvesNames.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //  System.out.println(pontoonInformations.getPontoonInfo().get(2));
        //  DockConfig.getPontoonInfoString();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         return valvesNames;

    }   // Get list of Valves names ********** Get list of Valves names *************************





}
