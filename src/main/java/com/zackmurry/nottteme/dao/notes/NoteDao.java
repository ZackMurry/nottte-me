package com.zackmurry.nottteme.dao.notes;

import com.zackmurry.nottteme.models.Note;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface NoteDao {

    HttpStatus updateNote(String title, String author, String content);

    long getIdByTitleAndAuthor(String title, String author);

    HttpStatus createNote(String title, String body, String author);

    boolean noteWithNameExists(String title);

    boolean userHasNote(String title, String username);

    String getRawNote(String title, String author);

    List<Note> getNotesByUser(String username);

    HttpStatus deleteNote(String title, String username) throws NotFoundException;

    HttpStatus renameNote(String oldTitle, String newTitle, String username) throws NotFoundException;

    int getNoteCount(String username);

    HttpStatus deleteNotesByAuthor(String author);

    List<String> getRawNotesByIdList(List<Long> noteIds);

    List<Note> getNotesByIdList(List<Long> noteIds);

}
