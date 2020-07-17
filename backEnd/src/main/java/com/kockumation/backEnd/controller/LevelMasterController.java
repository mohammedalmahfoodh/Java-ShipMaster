package com.kockumation.backEnd.controller;

import com.kockumation.backEnd.model.Alarm;
import com.kockumation.backEnd.model.LevelPostObject;
import com.kockumation.backEnd.services.LevelMasterService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class LevelMasterController {
    @Autowired
    LevelMasterService levelMasterService;

    // Accept Temp Alarm  ****************   Accept Temp Alarm   *********************
    @PostMapping("/acceptTempAlarm")
    public ResponseEntity postController(@RequestBody @Valid LevelPostObject levelPostObject) {

       boolean acceptedOrNot =  levelMasterService.makeTempAlarmAcknowledged(levelPostObject.getTank_id());
       if (acceptedOrNot){
           return ResponseEntity.ok(HttpStatus.OK);
       }else {
           return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
       }

    }// Accept Temp Alarm  ****************   Accept Temp Alarm   *********************

    // Get hundred Alarms  ****************   Get hundred Alarms   *********************
    @GetMapping("/hundredAlarms")
    public @ResponseBody ResponseEntity<List<Alarm>> getHundredAlarms() {
     List<Alarm>alarmList = new ArrayList<>();
        try {
            alarmList =levelMasterService.getHundredAlarms().get();
            if (alarmList.size() == 0){
                System.out.println("No Alarms");
                return new ResponseEntity<List<Alarm>>(alarmList, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<List<Alarm>>(alarmList, HttpStatus.OK);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity<List<Alarm>>(alarmList, HttpStatus.BAD_REQUEST);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<List<Alarm>>(alarmList, HttpStatus.BAD_REQUEST);
        }

    }// Get hundred Alarms  ****************   Get hundred Alarms   *********************

    // Accept Temp Alarm  ****************   Accept Temp Alarm   *********************
    @PostMapping("/updateTankLAHA")
    public ResponseEntity<String> updateTankLowAndHighLimit(@RequestBody @Valid LevelPostObject levelPostObject) {


        try {
             levelMasterService.updateTankLowAndHighLimit(levelPostObject).get();
            return new ResponseEntity<>("Tank id "+levelPostObject.getTank_id()+" High and low updated ",HttpStatus.OK);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Tank id "+levelPostObject.getTank_id()+" Bad Request ",HttpStatus.BAD_REQUEST);
        } catch (ExecutionException e) {
            return new ResponseEntity<>("Tank id "+levelPostObject.getTank_id()+" Bad Request ",HttpStatus.BAD_REQUEST);
        }


    }// Accept Temp Alarm  ****************   Accept Temp Alarm   *********************

    // Accept Temp Alarm  ****************   Accept Temp Alarm   *********************
    @PostMapping("/updateTankDensity")
    public ResponseEntity<String> updateTankDensity(@RequestBody @Valid LevelPostObject levelPostObject) {


        try {
            levelMasterService.updateTankDensity(levelPostObject).get();
            return new ResponseEntity<>("Tank id "+levelPostObject.getTank_id()+" density updated ",HttpStatus.OK);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Tank id "+levelPostObject.getTank_id()+" Bad Request ",HttpStatus.BAD_REQUEST);
        } catch (ExecutionException e) {
            return new ResponseEntity<>("Tank id "+levelPostObject.getTank_id()+" Bad Request ",HttpStatus.BAD_REQUEST);
        }


    }// Accept Temp Alarm  ****************   Accept Temp Alarm   *********************

}
