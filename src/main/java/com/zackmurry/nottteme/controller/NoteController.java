package com.zackmurry.nottteme.controller;

import com.google.gson.Gson;
import com.zackmurry.nottteme.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * rest controller for notes
 */
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/notes")
@RestController
public class NoteController {

    @Autowired
    private NoteService noteService;

    private Gson gson = new Gson();

    /**
     * placeholder for tests
     * todo saving and loading
     * todo this seems to always be one step behind of the actual saves
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
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return noteService.saveNote(title, SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), request);
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> create(@RequestBody String title) {
        if(title.contains("\"")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //because of JSON. todo show on website
        return noteService.createNote(title, "", SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }


}
