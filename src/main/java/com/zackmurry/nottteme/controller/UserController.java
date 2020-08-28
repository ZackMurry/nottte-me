package com.zackmurry.nottteme.controller;

import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.KeyboardShortcut;
import com.zackmurry.nottteme.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/create")
    public ResponseEntity<?> createUserAccount(@RequestBody User user) {
        if(user.getPassword() == null) return new ResponseEntity<HttpStatus>(HttpStatus.LENGTH_REQUIRED);
        if(user.getPassword().length() > 40) return new ResponseEntity<HttpStatus>(HttpStatus.LENGTH_REQUIRED); //bcrypt has a length limit
        //encoding password so that it's never stored in plain text
        //encoder automatically salts it
        String encodedPassword = encoder.encode(user.getPassword());

        boolean create = userService.createUserAccount(user.getUsername(), encodedPassword);
        if(create) return new ResponseEntity<HttpStatus>(HttpStatus.OK);
        else return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/exists/{username}")
    public boolean usernameExists(@PathVariable String username) {
        return userService.usernameExists(username);
    }

    @GetMapping("/user/{username}")
    public Optional<User> getUserByName(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    //preferences

    @PostMapping("/user/{username}/preferences/shortcuts")
    public ResponseEntity<HttpStatus> addKeyboardShortcut(@PathVariable String username, @RequestBody KeyboardShortcut keyboardShortcut) {
        return userService.addKeyboardShortcut(username, keyboardShortcut.getName(), keyboardShortcut.getText(), keyboardShortcut.getKeyCode());
    }

    @PostMapping("/principal/preferences/shortcuts")
    public ResponseEntity<HttpStatus> addKeyboardShortcutToPrincipal(@RequestBody KeyboardShortcut keyboardShortcut) {
        return userService.addKeyboardShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), keyboardShortcut.getName(), keyboardShortcut.getText(), keyboardShortcut.getKeyCode());
    }

    @GetMapping("/user/{username}/preferences/shortcuts")
    public List<KeyboardShortcut> getKeyboardShortcutsByUsername(@PathVariable String username) {
        return userService.getKeyboardShortcutsByUsername(username);
    }

    @GetMapping("/principal/preferences/shortcuts")
    public List<KeyboardShortcut> getKeyboardShortcutsOfPrincipal() {
        return userService.getKeyboardShortcutsByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/user/{username}/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> deleteUserKeyboardShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName) {
        return userService.deleteKeyboardShortcutByName(username, shortcutName);
    }

    @DeleteMapping("/principal/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> deletePrincipalKeyboardShortcutByName(@PathVariable("shortcutName") String shortcutName) {
        return userService.deleteKeyboardShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
    }

}
