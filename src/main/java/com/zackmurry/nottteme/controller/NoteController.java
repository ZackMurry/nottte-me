package com.zackmurry.nottteme.controller;

import com.google.gson.Gson;
import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.models.CreateNoteRequest;
import com.zackmurry.nottteme.models.CreateNoteWithBodyRequest;
import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.models.notes.RawNotePatch;
import com.zackmurry.nottteme.services.NoteService;
import com.zackmurry.nottteme.utils.NoteUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * rest controller for notes
 */
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/notes")
@RestController
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> create(@RequestBody CreateNoteRequest request) {
        HttpStatus status = noteService.createNote(request.getTitle(), NoteUtils.getBlankNoteBody(), SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(status);
    }

    @PostMapping("/create/with-body")
    public ResponseEntity<HttpStatus> createWithBody(@RequestBody CreateNoteWithBodyRequest request) {
        HttpStatus status = noteService.createNote(request.getTitle(), request.getBody(), SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(status);
    }

    @GetMapping("/note/{title}/raw")
    public String getRawNote(@PathVariable String title) throws NotFoundException, UnauthorizedException, UnsupportedEncodingException {
        //i think spring decodes URLs sometimes but sometimes it breaks itself
        try {
            title = URLDecoder.decode(title, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new UnsupportedEncodingException("Unable to decode title from encoded title \"" + title + "\"");
        }

        //if no note exists with this name, return 404
        if(!noteService.noteWithNameExists(title)) {
            throw new NotFoundException("Cannot find note with name " + title);
        }

        //if a note does exist, but user doesn't have the right authorization
        //this returns "anonymousUser" if unauthenticated
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!noteService.userHasNote(title, username)) {
            throw new UnauthorizedException("Note found with name " + title + ", but user does not have required authorization");
        }

        //since the user has the note they're requesting, return it
        return noteService.getRawNote(title, username);
    }

    @GetMapping("/principal/notes")
    public List<Note> getNotesFromPrincipal() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return noteService.getNotesByUser(username);
    }

    @DeleteMapping("/principal/note/{noteName}")
    public ResponseEntity<HttpStatus> deleteNoteOfPrincipalByName(@PathVariable("noteName") String noteName) {
        HttpStatus status = noteService.deleteNote(noteName, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(status);
    }

    @PatchMapping("/principal/note/{title}/rename/{newTitle}")
    public ResponseEntity<HttpStatus> renamePrincipalNote(@PathVariable("title") String oldTitle, @PathVariable("newTitle") String newTitle) {
         HttpStatus status = noteService.renameNote(oldTitle, newTitle, SecurityContextHolder.getContext().getAuthentication().getName());
         return new ResponseEntity<>(status);
    }

    @GetMapping("/count")
    public int getNoteCountFromPrincipal() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return noteService.getNoteCount(username);
    }

    @GetMapping("/principal/has/{title}")
    public boolean principalHasNote(@PathVariable("title") String title) {
        return noteService.userHasNote(title, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/principal/note/{title}/duplicate")
    public ResponseEntity<String> duplicateNote(@PathVariable("title") String title) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return noteService.duplicateNote(title, username);
    }

    /**
     * saves note using JSON diff
     * @param title title of note
     * @param patch body of patch, which details what needs to be changed
     * @return HttpStatus of action
     */
    @PatchMapping("/save/{title}")
    public HttpStatus patchNote(@PathVariable String title, @RequestBody RawNotePatch patch) {
        System.out.println("patch: " + new Gson().toJson(patch));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return noteService.patchNote(title, username, patch);
    }

}
