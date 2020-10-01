package com.zackmurry.nottteme.controller.sharing;

import com.zackmurry.nottteme.models.NoteIdentifier;
import com.zackmurry.nottteme.models.sharing.LinkShare;
import com.zackmurry.nottteme.models.sharing.LinkShareRequest;
import com.zackmurry.nottteme.models.sharing.ShareAuthority;
import com.zackmurry.nottteme.services.LinkShareService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shares/link")
public class LinkShareController {

    @Autowired
    private LinkShareService linkShareService;

    @PostMapping("/principal/create")
    public ResponseEntity<HttpStatus> createShareableLink(@RequestBody @NotNull LinkShareRequest request) {
        try {
            ShareAuthority.valueOf(request.getAuthority());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        request.setAuthor(username);
        HttpStatus status = linkShareService.createShareableLink(request);
        return new ResponseEntity<>(status);
    }

    @GetMapping("/principal/{id}")
    public NoteIdentifier useShareableLink(@PathVariable UUID id) throws NotFoundException, SQLException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return linkShareService.useShareableLink(id, username);
    }

    @PostMapping("/user/{username}/create")
    public ResponseEntity<HttpStatus> createShareableLinkByUser(@PathVariable String username, @RequestBody @NotNull LinkShareRequest request) {
        try {
            ShareAuthority.valueOf(request.getAuthority());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        request.setAuthor(username);
        HttpStatus status = linkShareService.createShareableLink(request);
        return new ResponseEntity<>(status);
    }

    @GetMapping("/user/{username}/{id}")
    public NoteIdentifier useShareableLinkAsUser(@PathVariable String username, @PathVariable UUID id) throws NotFoundException, SQLException {
        return linkShareService.useShareableLink(id, username);
    }

    @DeleteMapping("/principal/{id}")
    public ResponseEntity<HttpStatus> disableSharableLink(@PathVariable UUID id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HttpStatus status = linkShareService.disableSharableLink(id, username);
        return new ResponseEntity<>(status);
    }

    @GetMapping("/principal/note/{noteName}")
    public List<LinkShare> getShareableLinksOfNote(@PathVariable String noteName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return linkShareService.getShareableLinksFromNote(noteName, username);
    }

    @GetMapping("/user/{username}/note/{noteName}")
    public List<LinkShare> getShareableLinksOfNote(@PathVariable("username") String username, @PathVariable("noteName") String noteName) {
        return linkShareService.getShareableLinksFromNote(noteName, username);
    }

    @PatchMapping("/principal/{id}")
    public ResponseEntity<HttpStatus> updateSharableLink(@PathVariable UUID id, @RequestBody LinkShare linkShare) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        HttpStatus status = linkShareService.updateSharableLink(id, linkShare, username);
        return new ResponseEntity<>(status);
    }

    //keep this open to unauthenticated users, too
    @GetMapping("/id/{id}/note")
    public NoteIdentifier getNoteIdentifierByLinkShareId(@PathVariable @NotNull UUID id) throws NotFoundException, SQLException {
        return linkShareService.getNoteIdentifierById(id);
    }

    @GetMapping("/id/{id}/share")
    public LinkShare getLinkShareById(@PathVariable @NotNull UUID id) throws NotFoundException, SQLException {
        return linkShareService.getLinkShareById(id);
    }


}
