package com.zackmurry.nottteme.controller;

import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.services.ShareService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * for now, sharing will only work with view access for recipients
 */
@RequestMapping("/api/v1/shares")
@RestController
public class ShareController {

    @Autowired
    private ShareService shareService;

    @PostMapping("/principal/share/{title}/{recipientUsername}")
    public ResponseEntity<HttpStatus> shareNoteWithUser(@PathVariable("title") String title, @PathVariable("recipientUsername") String recipient) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HttpStatus status = shareService.shareNoteWithUser(username, title, recipient);
        System.out.println(username + ", " + title + ", " + recipient);
        return new ResponseEntity<>(status);
    }

    @PostMapping("/user/{username}/share/{title}/{recipientUsername}")
    public ResponseEntity<HttpStatus> shareUserNoteWithUser(@PathVariable("username") String username, @PathVariable("title") String title, @PathVariable("recipientUsername") String recipient) {
        HttpStatus status = shareService.shareNoteWithUser(username, title, recipient);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/principal/share/{title}/{recipientUsername}")
    public ResponseEntity<HttpStatus> unshareNoteWithUser(@PathVariable("title") String title, @PathVariable("recipientUsername") String recipient) throws NotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HttpStatus status = shareService.unshareNoteWithUser(username, title, recipient);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/user/{username}/share/{title}/{recipientUsername}")
    public ResponseEntity<HttpStatus> unshareUserNoteWithUser(@PathVariable("username") String username, @PathVariable("title") String title, @PathVariable("recipientUsername") String recipient) throws NotFoundException {
        HttpStatus status = shareService.unshareNoteWithUser(username, title, recipient);
        return new ResponseEntity<>(status);
    }


    @GetMapping("/principal/note/{title}/shares")
    public List<String> getSharesOfNote(@PathVariable("title") String title) throws NotFoundException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return shareService.getSharesOfNote(username, title);
    }

    @GetMapping("/user/{username}/note/{title}/shares")
    public List<String> getSharesOfUserNote(@PathVariable("username") String username, @PathVariable("title") String title) throws NotFoundException {
        return shareService.getSharesOfNote(username, title);
    }

    @GetMapping("/principal/note/{author}/{title}/access")
    public boolean principalHasAccessToNote(@PathVariable("title") String title, @PathVariable("author") String author) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return shareService.noteIsSharedWithUser(title, author, username);
    }

    @GetMapping("/user/{username}/note/{author}/{title}/access")
    public boolean principalHasAccessToNote(@PathVariable("username") String username, @PathVariable("title") String title, @PathVariable("author") String author) {
        return shareService.noteIsSharedWithUser(title, author, username);
    }

    @GetMapping("/user/{username}/note/{author}/{title}")
    public String getRawContentsOfSharedNoteWithUser(@PathVariable("username") String username, @PathVariable("title") String title, @PathVariable("author") String author) throws NotFoundException, UnauthorizedException {
        return shareService.getSharedNote(title, author, username);
    }

    @GetMapping("/principal/note/{author}/{title}")
    public String getRawContentsOfNoteSharedWithPrincipal(@PathVariable("author") String author, @PathVariable("title") String title) throws NotFoundException, UnauthorizedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return shareService.getSharedNote(title, author, username);
    }

}
