package com.zackmurry.nottteme.controller;

import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.services.NoteService;
import com.zackmurry.nottteme.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

//todo might want to only show a preview of the notes on the notes page for performance reasons (don't load full text)
//todo deleting accounts
//todo changing account name
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createUserAccount(@RequestBody User user) {
        if(user.getPassword() == null) return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        if(user.getPassword().length() > 40) return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED); //bcrypt has a length limit
        //encoding password so that it's never stored in plain text
        //encoder automatically salts it
        String encodedPassword = encoder.encode(user.getPassword());

        System.out.println(user.getEmail());
        boolean create = userService.createUserAccount(user.getUsername(), encodedPassword, user.getEmail());
        if(create) return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * needs to be open to all authenticated because of sharing
     */
    @GetMapping("/exists/{username}")
    public boolean usernameExists(@PathVariable String username) {
        return userService.accountExists(username);
    }

    @GetMapping("/user/{username}")
    public Optional<User> getUserByName(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @GetMapping("/principal")
    public Optional<User> getPrincipalObject() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByUsername(username);
    }

    /**
     * deletes current account
     * @return http status of whether or not it was successful
     */
    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteUserAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //deleting account from users table
        HttpStatus deleteAccountStatus = userService.deleteAccount(username);
        if(deleteAccountStatus.value() != 200) {
            return new ResponseEntity<>(deleteAccountStatus);
        }

        //deleting all the user's notes
        HttpStatus deleteNotesStatus = noteService.deleteNotesByAuthor(username);
        return new ResponseEntity<>(deleteNotesStatus);
    }

    @PostMapping("/user/{username}/email")
    public ResponseEntity<HttpStatus> updateUserAccountEmail(@PathVariable("username") String username, @RequestBody String email) {
        HttpStatus status = userService.updateEmail(username, email);
        return new ResponseEntity<>(status);
    }

    @PostMapping("/principal/email")
    public ResponseEntity<HttpStatus> updatePrincipalAccountEmail(@RequestBody String email) {
        HttpStatus status = userService.updateEmail(SecurityContextHolder.getContext().getAuthentication().getName(), email);
        return new ResponseEntity<>(status);
    }

}
