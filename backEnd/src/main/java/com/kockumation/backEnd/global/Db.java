package com.kockumation.backEnd.global;

import com.kockumation.backEnd.ValvesMaster.model.ValveDataForMap;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;

import java.util.HashMap;
import java.util.Map;

public class Db {

    public static Map<Integer, ValveDataForMap> valveMapData = new HashMap<>();
    public static Map<Integer, TankDataForMap> tankMapData = new HashMap<Integer, TankDataForMap>();

    public static void iterateValvesMap() {
        valveMapData.entrySet().stream().forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));

    }

    public static void printMapData() {
        for (Map.Entry<Integer, TankDataForMap> entry : Db.tankMapData.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }


}
