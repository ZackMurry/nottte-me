package com.zackmurry.nottteme.controller.shortcuts;

import com.zackmurry.nottteme.models.KeyboardShortcut;
import com.zackmurry.nottteme.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * rest controller for adding, getting, editing, and removing keyboard shortcuts (defined as shortcuts that insert text)
 * todo maybe rename keyboard shortcuts to text shortcuts
 */
@RestController
@RequestMapping("/api/v1/users")
public class KeyboardShortcutController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/{username}/preferences/shortcuts")
    public ResponseEntity<HttpStatus> addKeyboardShortcut(@PathVariable String username, @RequestBody KeyboardShortcut keyboardShortcut) {
        return userService.addKeyboardShortcut(username, keyboardShortcut.getName(), keyboardShortcut.getText(), keyboardShortcut.getKey());
    }

    @PostMapping("/principal/preferences/shortcuts")
    public ResponseEntity<HttpStatus> addKeyboardShortcutToPrincipal(@RequestBody KeyboardShortcut keyboardShortcut) {
        return userService.addKeyboardShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), keyboardShortcut.getName(), keyboardShortcut.getText(), keyboardShortcut.getKey());
    }

    @GetMapping("/user/{username}/preferences/shortcuts")
    public List<KeyboardShortcut> getKeyboardShortcutsByUsername(@PathVariable String username) {
        return userService.getKeyboardShortcutsByUsername(username);
    }

    @GetMapping("/principal/preferences/shortcuts")
    public List<KeyboardShortcut> getKeyboardShortcutsOfPrincipal() {
        return userService.getKeyboardShortcutsByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    //not really sure if entirely necessary
    @GetMapping("/user/{username}/preferences/shortcuts-sorted")
    public List<KeyboardShortcut> getKeyboardShortcutsByUsernameOrderedByName(@PathVariable String username) {
        return userService.getKeyboardShortcutsByUsernameOrderedByName(username);
    }

    @GetMapping("/principal/preferences/shortcuts-sorted")
    public List<KeyboardShortcut> getKeyboardShortcutsOfPrincipalOrderedByName() {
        return userService.getKeyboardShortcutsByUsernameOrderedByName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/user/{username}/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> deleteUserKeyboardShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName) {
        return userService.deleteKeyboardShortcutByName(username, shortcutName);
    }

    @DeleteMapping("/principal/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> deletePrincipalKeyboardShortcutByName(@PathVariable("shortcutName") String shortcutName) {
        return userService.deleteKeyboardShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
    }

    @PatchMapping("/user/{username}/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> updateUserKeyboardShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName, @RequestBody KeyboardShortcut updatedKeyboardShortcut) {
        return userService.updateKeyboardShortcutByName(username, shortcutName, updatedKeyboardShortcut);
    }

    @PatchMapping("/principal/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> updatePrincipalKeyboardShortcutByName(@PathVariable("shortcutName") String shortcutName, @RequestBody KeyboardShortcut updatedKeyboardShortcut) {
        return userService.updateKeyboardShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, updatedKeyboardShortcut);
    }



}
