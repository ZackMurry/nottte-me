package com.zackmurry.nottteme.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    /**
     * test method for getting principal's username
     * @return principal's username if successful, else error
     */
    @GetMapping("/user")
    public String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return "error";

    }

}
