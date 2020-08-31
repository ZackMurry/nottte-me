package com.zackmurry.nottteme.controller.shortcuts;

import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * todo default style and text shortcuts
 */
@RestController
@RequestMapping("/api/v1/users")
public class StyleShortcutController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{username}/preferences/shortcuts/style")
    public List<StyleShortcut> getStyleShortcutsByUsername(@PathVariable String username) {
        return userService.getStyleShortcutsByUsername(username);
    }

    @GetMapping("/principal/preferences/shortcuts/style")
    public List<StyleShortcut> getStyleShortcutsOfPrincipal() {
        return userService.getStyleShortcutsByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/user/{username}/preferences/shortcuts/style")
    public ResponseEntity<HttpStatus> addKeyboardShortcut(@PathVariable String username, @RequestBody StyleShortcut styleShortcut) {
        return userService.addStyleShortcut(username, styleShortcut.getName(), styleShortcut.getKey(), styleShortcut.getAttribute(), styleShortcut.getValue());
    }

    @PostMapping("/principal/preferences/shortcuts/style")
    public ResponseEntity<HttpStatus> addKeyboardShortcutToPrincipal(@RequestBody StyleShortcut styleShortcut) {
        return userService.addStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), styleShortcut.getName(), styleShortcut.getKey(), styleShortcut.getAttribute(), styleShortcut.getValue());
    }

    @DeleteMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> deleteUserKeyboardShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName) {
        return userService.deleteStyleShortcutByName(username, shortcutName);
    }

    @DeleteMapping("/principal/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> deletePrincipalKeyboardShortcutByName(@PathVariable("shortcutName") String shortcutName) {
        return userService.deleteStyleShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
    }

    @PatchMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> updateUserStyleShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName, @RequestBody StyleShortcut updatedStyleShortcut) {
        return userService.updateStyleShortcutByName(username, shortcutName, updatedStyleShortcut);
    }

    @PatchMapping("/principal/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> updatePrincipalKeyboardShortcutByName(@PathVariable("shortcutName") String shortcutName, @RequestBody StyleShortcut updatedStyleShortcut) {
        return userService.updateStyleShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, updatedStyleShortcut);
    }

}