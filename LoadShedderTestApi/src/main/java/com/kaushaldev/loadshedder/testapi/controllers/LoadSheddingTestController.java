package com.kaushaldev.loadshedder.testapi.controllers;

import com.kaushaldev.loadshedder.core.annotation.RequestThrottler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
public class LoadSheddingTestController {
    @RequestMapping("/")
    @RequestThrottler(limitPerSecond = 10)
    public String index() {
        System.out.println("Success : " + ZonedDateTime.now().toString());
        return "Success result from LoadSheddingTestController!";
    }
}
