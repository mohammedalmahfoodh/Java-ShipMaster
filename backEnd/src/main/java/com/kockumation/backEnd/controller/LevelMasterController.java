package com.kockumation.backEnd.controller;

import com.kockumation.backEnd.model.LevelPostObject;
import com.kockumation.backEnd.services.LevelMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LevelMasterController {
    @Autowired
    LevelMasterService levelMasterService;
    @PostMapping("/acceptTempAlarm")
    public ResponseEntity postController(@RequestBody LevelPostObject levelPostObject) {

        levelMasterService.makeTempAlarmAcknowledged(levelPostObject.getTank_id());
        return ResponseEntity.ok(HttpStatus.OK);
    }


}
