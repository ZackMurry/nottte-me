package com.zackmurry.nottteme.controller.shortcuts;

import com.zackmurry.nottteme.models.TextShortcut;
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
public class TextShortcutController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/{username}/preferences/shortcuts")
    public ResponseEntity<HttpStatus> addTextShortcut(@PathVariable String username, @RequestBody TextShortcut textShortcut) {
        return userService.addTextShortcut(username, textShortcut.getName(), textShortcut.getText(), textShortcut.getKey());
    }

    @PostMapping("/principal/preferences/shortcuts")
    public ResponseEntity<HttpStatus> addTextShortcutToPrincipal(@RequestBody TextShortcut textShortcut) {
        return userService.addTextShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), textShortcut.getName(), textShortcut.getText(), textShortcut.getKey());
    }

    @GetMapping("/user/{username}/preferences/shortcuts")
    public List<TextShortcut> getTextShortcutsByUsername(@PathVariable String username) {
        return userService.getTextShortcutsByUsername(username);
    }

    @GetMapping("/principal/preferences/shortcuts")
    public List<TextShortcut> getTextShortcutsOfPrincipal() {
        return userService.getTextShortcutsByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    //not really sure if entirely necessary
    @GetMapping("/user/{username}/preferences/shortcuts-sorted")
    public List<TextShortcut> getTextShortcutsByUsernameOrderedByName(@PathVariable String username) {
        return userService.getTextShortcutsByUsernameOrderedByName(username);
    }

    @GetMapping("/principal/preferences/shortcuts-sorted")
    public List<TextShortcut> getTextShortcutsOfPrincipalOrderedByName() {
        return userService.getTextShortcutsByUsernameOrderedByName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/user/{username}/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> deleteUserTextShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName) {
        return userService.deleteTextShortcutByName(username, shortcutName);
    }

    @DeleteMapping("/principal/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> deletePrincipalTextShortcutByName(@PathVariable("shortcutName") String shortcutName) {
        return userService.deleteTextShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
    }

    @PatchMapping("/user/{username}/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> updateUserTextShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName, @RequestBody TextShortcut updatedTextShortcut) {
        return userService.updateTextShortcutByName(username, shortcutName, updatedTextShortcut);
    }

    @PatchMapping("/principal/preferences/shortcuts/{shortcutName}")
    public ResponseEntity<HttpStatus> updatePrincipalTextShortcutByName(@PathVariable("shortcutName") String shortcutName, @RequestBody TextShortcut updatedTextShortcut) {
        return userService.updateTextShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, updatedTextShortcut);
    }



}
