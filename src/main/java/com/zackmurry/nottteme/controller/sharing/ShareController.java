package com.zackmurry.nottteme.controller.sharing;

import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.models.shortcuts.StyleShortcut;
import com.zackmurry.nottteme.services.NoteService;
import com.zackmurry.nottteme.services.ShareService;
import com.zackmurry.nottteme.services.ShortcutService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * for now, sharing will only work with view access for recipients
 * todo change GET /principal/note/{title}/shares return type to list of NoteShares
 * setting another user as owner of a note
 */
@RequestMapping("/api/v1/shares")
@RestController
public class ShareController {

    @Autowired
    private ShareService shareService;

    @Autowired
    private ShortcutService shortcutService;

    @Autowired
    private NoteService noteService;

    @PostMapping("/principal/share/{title}/{recipientUsername}")
    public ResponseEntity<HttpStatus> shareNoteWithUser(@PathVariable("title") String title, @PathVariable("recipientUsername") String recipient) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HttpStatus status = shareService.shareNoteWithUser(username, title, recipient);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/principal/share/{title}/{recipientUsername}")
    public ResponseEntity<HttpStatus> unshareNoteWithUser(@PathVariable("title") String title, @PathVariable("recipientUsername") String recipient) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HttpStatus status = shareService.unshareNoteWithUser(username, title, recipient);
        return new ResponseEntity<>(status);
    }

    @GetMapping("/principal/note/{title}/shares")
    public List<String> getSharesOfNote(@PathVariable("title") String title) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
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

    @GetMapping("/principal/note/{author}/{title}/raw")
    public String getRawContentsOfNoteSharedWithPrincipal(@PathVariable("author") String author, @PathVariable("title") String title) throws NotFoundException, UnauthorizedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return shareService.getRawSharedNote(title, author, username);
    }

    @GetMapping("/principal/note/{author}/{title}/shortcuts/style")
    public List<StyleShortcut> getStyleShortcutsOfAuthorOfNoteSharedWithPrincipal(@PathVariable("author") String author, @PathVariable("title") String title) throws UnauthorizedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!shareService.noteIsSharedWithUser(title, author, username)) throw new UnauthorizedException("User does not have access to this note.");
        return shortcutService.getStyleShortcutsByUsername(author);
    }

    @GetMapping("/user/{username}/note/{author}/{title}/shortcuts/style")
    public List<StyleShortcut> getStyleShortcutsOfAuthorOfNoteSharedWithPrincipal(@PathVariable("username") String username, @PathVariable("author") String author, @PathVariable("title") String title) throws UnauthorizedException {
        if(!shareService.noteIsSharedWithUser(title, author, username)) throw new UnauthorizedException("User does not have access to this note.");
        return shortcutService.getStyleShortcutsByUsername(author);
    }

    @GetMapping("/principal/note/{author}/{title}/shares")
    public List<String> getSharesOfNoteSharedWithPrincipal(@PathVariable("author") String author, @PathVariable("title") String title) throws UnauthorizedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!shareService.noteIsSharedWithUser(title, author, username)) throw new UnauthorizedException("User does not have access to this note.");
        return shareService.getSharesOfNote(author, title);
    }

    @GetMapping("/principal/shared-notes")
    public List<Note> getNotesSharedWithPrincipal() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Long> noteIds = shareService.getNoteIdsSharedWithUser(username);
        if(noteIds.size() == 0) return new ArrayList<>();
        return noteService.getNotesByIdList(noteIds);
    }

    @PostMapping("/principal/note/{author}/{title}/duplicate")
    public ResponseEntity<String> duplicateNoteSharedWithPrincipal(@PathVariable("author") String author, @PathVariable("title") String title) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return shareService.duplicateSharedNote(author, title, username);
    }

    @GetMapping("/principal/shortcuts")
    public List<StyleShortcut> getSharedStyleShortcutsOfPrincipal() {
        return shortcutService.getSharedStyleShortcutsByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
