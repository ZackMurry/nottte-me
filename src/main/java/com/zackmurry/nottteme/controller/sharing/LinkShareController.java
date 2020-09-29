package com.zackmurry.nottteme.controller.sharing;

import com.zackmurry.nottteme.models.LinkShareRequest;
import com.zackmurry.nottteme.models.NoteIdentifier;
import com.zackmurry.nottteme.services.LinkShareService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shares/link")
public class LinkShareController {

    @Autowired
    private LinkShareService linkShareService;

    @PostMapping("/principal/create")
    public ResponseEntity<HttpStatus> createShareableLink(@RequestBody @NotNull LinkShareRequest request) {
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
        request.setAuthor(username);
        HttpStatus status = linkShareService.createShareableLink(request);
        return new ResponseEntity<>(status);
    }

    @GetMapping("/user/{username}/{id}")
    public NoteIdentifier useShareableLinkAsUser(@PathVariable String username, @PathVariable UUID id) throws NotFoundException, SQLException {
        return linkShareService.useShareableLink(id, username);
    }


}
