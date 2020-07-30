package com.kockumation.backEnd.services;

import com.kockumation.backEnd.ValvesMaster.model.ValveDataForMap;

import java.util.HashMap;
import java.util.Map;

public class Db {

    public static Map<Integer, ValveDataForMap> valveMapData = new HashMap<>();
    public static void iterateValvesMap() {
        valveMapData.entrySet().stream().forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));

    }

}
