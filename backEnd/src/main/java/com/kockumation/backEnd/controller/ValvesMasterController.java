package com.kockumation.backEnd.controller;

import com.kockumation.backEnd.model.LevelPostObject;
import com.kockumation.backEnd.model.ValvePostObject;
import com.kockumation.backEnd.services.LevelMasterService;
import com.kockumation.backEnd.services.ValvesMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
@RestController
@RequestMapping("/api")
public class ValvesMasterController {
    @Autowired
    ValvesMasterService valvesMasterService;



    // Accept Valve Alarm  ****************   Accept Valve Alarm   *********************
    @PostMapping("/acceptValve")
    public ResponseEntity<String> acceptValveAlarm(@RequestBody @Valid ValvePostObject valvePostObject) {

        try {
        boolean acceptedOrNot = valvesMasterService.makeAlarmAcknowledged(valvePostObject).get();
        if (acceptedOrNot){
            return new ResponseEntity<>("Valve id " + valvePostObject.getValve_id() + " Accepted", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Valve id " + valvePostObject.getValve_id() + " Not Accepted", HttpStatus.BAD_REQUEST);
        }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Tank id " + valvePostObject.getValve_id() + " Bad Request ", HttpStatus.BAD_REQUEST);
        } catch (ExecutionException e) {
            return new ResponseEntity<>("Tank id " + valvePostObject.getValve_id() + " Bad Request ", HttpStatus.BAD_REQUEST);
        }

    }// Accept Valve Alarm  ****************   Accept Valve Alarm   *********************
}
