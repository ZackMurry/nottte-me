package com.zackmurry.nottteme.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/free")
    public String free() {
        return "I'm free";
    }

    @GetMapping("/authed")
    public String authenticated() {
        return "I'm authenticated!";
    }

}
