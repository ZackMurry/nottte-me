package com.zackmurry.nottteme.controller;

import com.google.gson.Gson;
import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.models.CreateNoteRequest;
import com.zackmurry.nottteme.models.CreateNoteWithBodyRequest;
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
     * @param request object in JSON format (for ease of keeping in database). contains the post title and the editor state
     */
    @PatchMapping("/save")
    public ResponseEntity<HttpStatus> save(@RequestBody String request) {
        String title;

        //grabbing title from request and then substringing to the body part to keep that in the database
        //todo probably mad vulnerabilities in this
        if(request.startsWith("{\"title\":\"")) {
            //todo this is O(n) and not very cool
            request = request.substring(10, request.length()-1); //getting rid of the title part and the last brace
            StringBuilder titleBuilder = new StringBuilder();
            for (int i = 0; i < request.length(); i++) {
                char charAt = request.charAt(i);
                if(charAt == '"') {
                    if(!request.startsWith("\",\"body\":{\"blocks\":[{\"key\":\"", i)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    request = request.substring(i+9);
                    break;
                }
                titleBuilder.append(charAt);
            }
            title = titleBuilder.toString();
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return noteService.saveNote(title, SecurityContextHolder.getContext().getAuthentication().getName(), request);
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> create(@RequestBody CreateNoteRequest request) {
        if(request.getTitle().contains("\"")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //because of JSON. todo show on website
        return noteService.createNote(request.getTitle(), "", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/create/with-body")
    public ResponseEntity<HttpStatus> createWithBody(@RequestBody CreateNoteWithBodyRequest request) {
        if(request.getTitle().contains("\"")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //because of JSON. todo show on website
        return noteService.createNote(request.getTitle(), request.getBody(), SecurityContextHolder.getContext().getAuthentication().getName());
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
        return noteService.getRawNote(title, username);
    }


}
