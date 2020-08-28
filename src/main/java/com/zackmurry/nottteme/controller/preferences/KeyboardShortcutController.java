package com.zackmurry.nottteme.controller.preferences;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * used for storing data about users' keyboard shortcuts so that the frontend can fetch them
 */
@RestController
@RequestMapping("/api/v1/users")
public class KeyboardShortcutController {

    @PostMapping("/{username}/preferences/shortcuts")
    public ResponseEntity<HttpStatus> addKeyboardShortcut() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
