package com.zackmurry.nottteme.dao.notes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface NoteDao {

    ResponseEntity<HttpStatus> updateNote(String title, String author, String content);

    long getIdByTitleAndAuthor(String title, String author);

    ResponseEntity<HttpStatus> createNote(String title, String body, String author);

}
