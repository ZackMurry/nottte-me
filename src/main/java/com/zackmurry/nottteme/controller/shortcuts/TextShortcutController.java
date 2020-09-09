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
 * rest controller for adding, getting, editing, and removing text shortcuts (defined as shortcuts that insert text)
 */
@RestController
@RequestMapping("/api/v1/users")
public class TextShortcutController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/{username}/preferences/shortcuts/text")
    public ResponseEntity<HttpStatus> addTextShortcut(@PathVariable String username, @RequestBody TextShortcut textShortcut) {
        HttpStatus status = userService.addTextShortcut(username, textShortcut.getName(), textShortcut.getText(), textShortcut.getKey());
        return new ResponseEntity<>(status);
    }

    @PostMapping("/principal/preferences/shortcuts/text")
    public ResponseEntity<HttpStatus> addTextShortcutToPrincipal(@RequestBody TextShortcut textShortcut) {
        HttpStatus status = userService.addTextShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), textShortcut.getName(), textShortcut.getText(), textShortcut.getKey());
        return new ResponseEntity<>(status);
    }

    @GetMapping("/user/{username}/preferences/shortcuts/text")
    public List<TextShortcut> getTextShortcutsByUsername(@PathVariable String username) {
        return userService.getTextShortcutsByUsername(username);
    }

    @GetMapping("/principal/preferences/shortcuts/text")
    public List<TextShortcut> getTextShortcutsOfPrincipal() {
        return userService.getTextShortcutsByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/user/{username}/preferences/shortcuts/text-sorted")
    public List<TextShortcut> getTextShortcutsByUsernameOrderedByName(@PathVariable String username) {
        return userService.getTextShortcutsByUsernameOrderedByName(username);
    }

    @GetMapping("/principal/preferences/shortcuts/text-sorted")
    public List<TextShortcut> getTextShortcutsOfPrincipalOrderedByName() {
        return userService.getTextShortcutsByUsernameOrderedByName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/user/{username}/preferences/shortcuts/text/{shortcutName}")
    public ResponseEntity<HttpStatus> deleteUserTextShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName) {
        HttpStatus status = userService.deleteTextShortcutByName(username, shortcutName);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/principal/preferences/shortcuts/text/{shortcutName}")
    public ResponseEntity<HttpStatus> deletePrincipalTextShortcutByName(@PathVariable("shortcutName") String shortcutName) {
        HttpStatus status = userService.deleteTextShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
        return new ResponseEntity<>(status);
    }

    @PatchMapping("/user/{username}/preferences/shortcuts/text/{shortcutName}")
    public ResponseEntity<HttpStatus> updateUserTextShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName, @RequestBody TextShortcut updatedTextShortcut) {
        HttpStatus status = userService.updateTextShortcutByName(username, shortcutName, updatedTextShortcut);
        return new ResponseEntity<>(status);
    }

    @PatchMapping("/principal/preferences/shortcuts/text/{shortcutName}")
    public ResponseEntity<HttpStatus> updatePrincipalTextShortcutByName(@PathVariable("shortcutName") String shortcutName, @RequestBody TextShortcut updatedTextShortcut) {
        HttpStatus status = userService.updateTextShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, updatedTextShortcut);
        return new ResponseEntity<>(status);
    }



}
