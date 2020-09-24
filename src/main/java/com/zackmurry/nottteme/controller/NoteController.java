package com.zackmurry.nottteme.controller;

import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.models.CreateNoteRequest;
import com.zackmurry.nottteme.models.CreateNoteWithBodyRequest;
import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.services.NoteService;
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

    /**
     * todo authorization
     *
     * @param request object in JSON format (for ease of keeping in database). contains the raw editor state
     */
    @PatchMapping("/save/{title}")
    public ResponseEntity<HttpStatus> save(@PathVariable String title, @RequestBody String request) {

        //todo remove once i sort out authorization
        if(SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        HttpStatus status = noteService.saveNote(title, SecurityContextHolder.getContext().getAuthentication().getName(), request);
        return new ResponseEntity<>(status);
    }

    //todo no notes with % sign in title (bc of urls)
    @PostMapping("/create")
    public ResponseEntity<HttpStatus> create(@RequestBody CreateNoteRequest request) {
        if(request.getTitle().contains("\"")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //because of JSON. todo show on website
        HttpStatus status = noteService.createNote(request.getTitle(), "", SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(status);
    }

    @PostMapping("/create/with-body")
    public ResponseEntity<HttpStatus> createWithBody(@RequestBody CreateNoteWithBodyRequest request) {
        if(request.getTitle().contains("\"")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //because of JSON. todo show on website
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

        //todo can probably remove this check once i implement spring security for blocking requests
        if(username.equals("anonymousUser")) {
            throw new UnauthorizedException("Note found with name " + title + ", but user is unauthenticated");
        }
        else if(!noteService.userHasNote(title, username)) {
            throw new UnauthorizedException("Note found with name " + title + ", but user does not have required authorization");
        }

        //since the user has the note they're requesting, return it
        String raw = noteService.getRawNote(title, username);
        System.out.println(raw);
        return raw;
    }

    @GetMapping("/principal/notes")
    public List<Note> getNotesFromPrincipal() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return noteService.getNotesByUser(username);
    }

    @DeleteMapping("/user/{username}/note/{noteName}")
    public ResponseEntity<HttpStatus> deleteNoteOfUsernameByName(@PathVariable("username") String username, @PathVariable("noteName") String noteName) throws NotFoundException {
        HttpStatus status = noteService.deleteNote(noteName, username);
        return new ResponseEntity<>(status);
    }

    @DeleteMapping("/principal/note/{noteName}")
    public ResponseEntity<HttpStatus> deleteNoteOfPrincipalByName(@PathVariable("noteName") String noteName) throws NotFoundException {
        HttpStatus status = noteService.deleteNote(noteName, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(status);
    }

    @PatchMapping("/principal/note/{title}/rename/{newTitle}")
    public ResponseEntity<HttpStatus> renamePrincipalNote(@PathVariable("title") String oldTitle, @PathVariable("newTitle") String newTitle) throws NotFoundException {
         HttpStatus status = noteService.renameNote(oldTitle, newTitle, SecurityContextHolder.getContext().getAuthentication().getName());
         return new ResponseEntity<>(status);
    }

    @PatchMapping("/user/{username}/note/{title}/rename/{newTitle}")
    public ResponseEntity<HttpStatus> renameUserNote(@PathVariable("username") String username, @PathVariable("title") String title, @PathVariable("newTitle") String newTitle) throws NotFoundException {
        HttpStatus status = noteService.renameNote(title, newTitle, username);
        return new ResponseEntity<>(status);
    }

    @GetMapping("/count")
    public int getNoteCountFromPrincipal() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return noteService.getNoteCount(username);
    }

    @GetMapping("/user/{username}/notes/count")
    public int getNoteCountFromPrincipal(@PathVariable("username") String username) {
        return noteService.getNoteCount(username);
    }

    @GetMapping("/principal/has/{title}")
    public boolean principalHasNote(@PathVariable("title") String title) {
        return noteService.userHasNote(title, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/user/{username}/has/{title}")
    public boolean userHasNote(@PathVariable("username") String username, @PathVariable("title") String title) {
        return noteService.userHasNote(title, username);
    }

    @PostMapping("/user/{username}/note/{title}/duplicate")
    public HttpStatus duplicateNote(@PathVariable("username") String username, @PathVariable("title") String title) {
        return noteService.duplicateNote(title, username);
    }

    @PostMapping("/principal/note/{title}/duplicate")
    public HttpStatus duplicateNote(@PathVariable("title") String title) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return noteService.duplicateNote(title, username);
    }

}
