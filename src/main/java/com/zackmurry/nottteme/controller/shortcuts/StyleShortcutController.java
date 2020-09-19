package com.zackmurry.nottteme.controller.shortcuts;

import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.services.UserService;
import javassist.NotFoundException;
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
    public ResponseEntity<HttpStatus> addStyleShortcut(@PathVariable String username, @RequestBody StyleShortcut styleShortcut) {
        HttpStatus status = userService.addStyleShortcut(username, styleShortcut.getName(), styleShortcut.getKey(), styleShortcut.getAttributes(), styleShortcut.getAlt());
        return new ResponseEntity<>(status);
    }

    @PostMapping("/principal/preferences/shortcuts/style")
    public ResponseEntity<HttpStatus> addStyleShortcutToPrincipal(@RequestBody StyleShortcut styleShortcut) {
        HttpStatus status = userService.addStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), styleShortcut.getName(), styleShortcut.getKey(), styleShortcut.getAttributes(), styleShortcut.getAlt());
        return new ResponseEntity<>(status);
    }

    @GetMapping("/user/{username}/preferences/shortcuts/style-sorted")
    public List<StyleShortcut> getStyleShortcutsByUsernameOrderedByName(@PathVariable String username) {
        return userService.getStyleShortcutsByUsernameOrderedByName(username);
    }

    @GetMapping("/principal/preferences/shortcuts/style-sorted")
    public List<StyleShortcut> getStyleShortcutsOfPrincipalOrderedByName() {
        return userService.getStyleShortcutsByUsernameOrderedByName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> deleteUserStyleShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName) {
        HttpStatus status = userService.deleteStyleShortcutByName(username, shortcutName);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/principal/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> deletePrincipalStyleShortcutByName(@PathVariable("shortcutName") String shortcutName) {
        HttpStatus status = userService.deleteStyleShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
        return new ResponseEntity<>(status);
    }

    @PatchMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> updateUserStyleShortcutByName(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName, @RequestBody StyleShortcut updatedStyleShortcut) {
        HttpStatus status = userService.updateStyleShortcutByName(username, shortcutName, updatedStyleShortcut);
        return new ResponseEntity<>(status);
    }

    @PatchMapping("/principal/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> updatePrincipalStyleShortcutByName(@PathVariable("shortcutName") String shortcutName, @RequestBody StyleShortcut updatedStyleShortcut) {
        HttpStatus status = userService.updateStyleShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, updatedStyleShortcut);
        return new ResponseEntity<>(status);
    }

    @GetMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}")
    public StyleShortcut getUserStyleShortcut(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName) throws NotFoundException {
        return userService.getStyleShortcutByUsername(username, shortcutName);
    }

    @PostMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}/attributes")
    public ResponseEntity<HttpStatus> addCSSAttributeToUserStyleShortcut(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName, @RequestBody CSSAttribute attribute) {
        HttpStatus status = userService.addCSSAttributeToStyleShortcut(username, shortcutName, attribute);
        return new ResponseEntity<>(status);
    }

    @PostMapping("/principal/preferences/shortcuts/style/{shortcutName}/attributes")
    public ResponseEntity<HttpStatus> addCSSAttributeToPrincipalStyleShortcut(@PathVariable("shortcutName") String shortcutName, @RequestBody CSSAttribute attribute) {
        HttpStatus status = userService.addCSSAttributeToStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, attribute);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}/attribute/{attributeName}")
    public ResponseEntity<HttpStatus> removeCSSAttributeFromUserStyleShortcut(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName, @PathVariable("attributeName") String attributeName) {
        HttpStatus status = userService.removeCSSAttributeFromStyleShortcut(username, shortcutName, attributeName);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/principal/preferences/shortcuts/style/{shortcutName}/attribute/{attributeName}")
    public ResponseEntity<HttpStatus> removeCSSAttributeFromPrincipalStyleShortcut(@PathVariable("shortcutName") String shortcutName, @PathVariable("attributeName") String attributeName) {
        HttpStatus status = userService.removeCSSAttributeFromStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, attributeName);
        return new ResponseEntity<>(status);
    }

    //todo add endpoints for editing and getting CSS attributes from a style shortcut
    @GetMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}/attribute/{attributeName}")
    public CSSAttribute getCSSAttributeFromUserStyleShortcut(
            @PathVariable("username") String username,
            @PathVariable("shortcutName") String shortcutName,
            @PathVariable("attributeName") String attributeName) throws NotFoundException {
        return userService.getCSSAttributeFromStyleShortcut(username, shortcutName, attributeName);
    }

    @GetMapping("/principal/preferences/shortcuts/style/{shortcutName}/attribute/{attributeName}")
    public CSSAttribute getCSSAttributeFromPrincipalStyleShortcut(@PathVariable("shortcutName") String shortcutName, @PathVariable("attributeName") String attributeName) throws NotFoundException {
        return userService.getCSSAttributeFromStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, attributeName);
    }

    @GetMapping("/user/{username}/preferences/shortcuts/style/{shortcutName}/attributes")
    public List<CSSAttribute> getCSSAttributesFromUserStyleShortcut(@PathVariable("username") String username, @PathVariable("shortcutName") String shortcutName) throws NotFoundException {
        return userService.getCSSAttributesFromStyleShortcut(username, shortcutName);
    }

    @GetMapping("/principal/preferences/shortcuts/style/{shortcutName}/attributes")
    public List<CSSAttribute> getCSSAttributesFromPrincipalStyleShortcut(@PathVariable("shortcutName") String shortcutName) throws NotFoundException {
        return userService.getCSSAttributesFromStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
    }

}
