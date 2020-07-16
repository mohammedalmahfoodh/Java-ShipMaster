package com.kockumation.backEnd.services;

import com.kockumation.backEnd.levelMaster.DetectAndSaveAlarms;
import com.kockumation.backEnd.levelMaster.LavelMasterManager;
import com.kockumation.backEnd.levelMaster.model.TankDataForMap;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
@Service
public class LevelMasterService {
    DetectAndSaveAlarms detectAndSaveAlarms = new DetectAndSaveAlarms();

    //Make temp alarm Acknowledged ******************** Make temp alarm Acknowledged ****************************************************
    public boolean makeTempAlarmAcknowledged(int tank_id) {
        if (LavelMasterManager.tankMapData.containsKey(tank_id)) {
            TankDataForMap tankDataForMap = LavelMasterManager.tankMapData.get(tank_id);
            if (tankDataForMap.isTemp_alarm_active() == true || tankDataForMap.isTemp_blue_alarm() == true) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String temperatureAlarmDate = LocalDateTime.now(Clock.systemUTC()).format(formatter);
                tankDataForMap.setTemp_time_accepted(temperatureAlarmDate);
                tankDataForMap.setTemp_alarm_description("Temp Alarm Active accepted");
                tankDataForMap.setTemp_acknowledged(true);
                if (tankDataForMap.isTemp_inserted() == true) {

                    boolean insertedOrNot = false;
                    try {
                        insertedOrNot = detectAndSaveAlarms.updateTempAlarm(tankDataForMap).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (insertedOrNot) {

                        System.out.println("Temp Alarm for tank " + tankDataForMap.getCode_name() + " became Accepted");
                        return true;
                    } else {
                        System.out.println("Error connecting database connect to db to update alarms");
                        return false;
                    }
                }
            }

        } else {
            return false;
        }

        return true;
    }//Make temp alarm Acknowledged ******************** Make temp alarm Acknowledged ****************************************************


}
