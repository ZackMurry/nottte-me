package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.notes.NoteDao;
import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    @Autowired
    private NoteDao noteDao;


    public ResponseEntity<HttpStatus> createNote(String title, String body, String author) {
        return noteDao.createNote(title, body, author);
    }

    public ResponseEntity<HttpStatus> saveNote(String title, String author, String body) {
        return noteDao.updateNote(title, author, body);
    }

    public boolean noteWithNameExists(String title) {
        return noteDao.noteWithNameExists(title);
    }

    public boolean userHasNote(String title, String username) {
        return noteDao.userHasNote(title, username);
    }

    public String getRawNote(String title, String author) {
        return noteDao.getRawNote(title, author);
    }
}
