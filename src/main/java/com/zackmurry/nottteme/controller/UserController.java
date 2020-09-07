package com.zackmurry.nottteme.controller;

import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

//todo might want to only show a preview of the notes on the notes page for performance reasons (don't load full text)
//todo deleting accounts
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createUserAccount(@RequestBody User user) {
        if(user.getPassword() == null) return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        if(user.getPassword().length() > 40) return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED); //bcrypt has a length limit
        //encoding password so that it's never stored in plain text
        //encoder automatically salts it
        String encodedPassword = encoder.encode(user.getPassword());

        boolean create = userService.createUserAccount(user.getUsername(), encodedPassword);
        if(create) return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/exists/{username}")
    public boolean usernameExists(@PathVariable String username) {
        return userService.usernameExists(username);
    }

    @GetMapping("/user/{username}")
    public Optional<User> getUserByName(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

}
