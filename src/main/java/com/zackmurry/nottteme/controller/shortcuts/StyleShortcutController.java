package com.zackmurry.nottteme.controller.shortcuts;

import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.shortcuts.StyleShortcut;
import com.zackmurry.nottteme.services.ShortcutService;
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
    private ShortcutService shortcutService;

    @GetMapping("/principal/preferences/shortcuts/style")
    public List<StyleShortcut> getStyleShortcutsOfPrincipal() {
        return shortcutService.getStyleShortcutsByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/principal/preferences/shortcuts/style")
    public ResponseEntity<HttpStatus> addStyleShortcutToPrincipal(@RequestBody StyleShortcut styleShortcut) {
        HttpStatus status = shortcutService.addStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), styleShortcut.getName(), styleShortcut.getKey(), styleShortcut.getAttributes(), styleShortcut.getAlt());
        return new ResponseEntity<>(status);
    }

    @GetMapping("/principal/preferences/shortcuts/style-sorted")
    public List<StyleShortcut> getStyleShortcutsOfPrincipalOrderedByName() {
        return shortcutService.getStyleShortcutsByUsernameOrderedByName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/principal/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> deletePrincipalStyleShortcutByName(@PathVariable("shortcutName") String shortcutName) {
        HttpStatus status = shortcutService.deleteStyleShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
        return new ResponseEntity<>(status);
    }

    @PatchMapping("/principal/preferences/shortcuts/style/{shortcutName}")
    public ResponseEntity<HttpStatus> updatePrincipalStyleShortcutByName(@PathVariable("shortcutName") String shortcutName, @RequestBody StyleShortcut updatedStyleShortcut) {
        HttpStatus status = shortcutService.updateStyleShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, updatedStyleShortcut);
        return new ResponseEntity<>(status);
    }

    @PostMapping("/principal/preferences/shortcuts/style/{shortcutName}/attributes")
    public ResponseEntity<HttpStatus> addCSSAttributeToPrincipalStyleShortcut(@PathVariable("shortcutName") String shortcutName, @RequestBody CSSAttribute attribute) {
        HttpStatus status = shortcutService.addCSSAttributeToStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, attribute);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/principal/preferences/shortcuts/style/{shortcutName}/attribute/{attributeName}")
    public ResponseEntity<HttpStatus> removeCSSAttributeFromPrincipalStyleShortcut(@PathVariable("shortcutName") String shortcutName, @PathVariable("attributeName") String attributeName) {
        HttpStatus status = shortcutService.removeCSSAttributeFromStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, attributeName);
        return new ResponseEntity<>(status);
    }

    @GetMapping("/principal/preferences/shortcuts/style/{shortcutName}/attribute/{attributeName}")
    public CSSAttribute getCSSAttributeFromPrincipalStyleShortcut(@PathVariable("shortcutName") String shortcutName, @PathVariable("attributeName") String attributeName) throws NotFoundException {
        return shortcutService.getCSSAttributeFromStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, attributeName);
    }

    @GetMapping("/principal/preferences/shortcuts/style/{shortcutName}/attributes")
    public List<CSSAttribute> getCSSAttributesFromPrincipalStyleShortcut(@PathVariable("shortcutName") String shortcutName) throws NotFoundException {
        return shortcutService.getCSSAttributesFromStyleShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
    }

}
