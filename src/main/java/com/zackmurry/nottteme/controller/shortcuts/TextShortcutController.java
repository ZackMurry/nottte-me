package com.zackmurry.nottteme.controller.shortcuts;

import com.zackmurry.nottteme.models.shortcuts.TextShortcut;
import com.zackmurry.nottteme.services.ShortcutService;
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
    private ShortcutService shortcutService;

    @PostMapping("/principal/preferences/shortcuts/text")
    public ResponseEntity<HttpStatus> addTextShortcutToPrincipal(@RequestBody TextShortcut textShortcut) {
        HttpStatus status = shortcutService.addTextShortcut(SecurityContextHolder.getContext().getAuthentication().getName(), textShortcut.getName(), textShortcut.getText(), textShortcut.getKey(), textShortcut.getAlt());
        return new ResponseEntity<>(status);
    }

    @GetMapping("/principal/preferences/shortcuts/text")
    public List<TextShortcut> getTextShortcutsOfPrincipal() {
        return shortcutService.getTextShortcutsByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/principal/preferences/shortcuts/text-sorted")
    public List<TextShortcut> getTextShortcutsOfPrincipalOrderedByName() {
        return shortcutService.getTextShortcutsByUsernameOrderedByName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/principal/preferences/shortcuts/text/{shortcutName}")
    public ResponseEntity<HttpStatus> deletePrincipalTextShortcutByName(@PathVariable("shortcutName") String shortcutName) {
        HttpStatus status = shortcutService.deleteTextShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName);
        return new ResponseEntity<>(status);
    }

    @PatchMapping("/principal/preferences/shortcuts/text/{shortcutName}")
    public ResponseEntity<HttpStatus> updatePrincipalTextShortcutByName(@PathVariable("shortcutName") String shortcutName, @RequestBody TextShortcut updatedTextShortcut) {
        HttpStatus status = shortcutService.updateTextShortcutByName(SecurityContextHolder.getContext().getAuthentication().getName(), shortcutName, updatedTextShortcut);
        return new ResponseEntity<>(status);
    }

}
