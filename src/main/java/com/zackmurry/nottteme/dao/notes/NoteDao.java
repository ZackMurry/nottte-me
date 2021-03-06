package com.zackmurry.nottteme.dao.notes;

import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.models.NoteIdentifier;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface NoteDao {

    HttpStatus updateNote(String title, String author, String content);

    long getIdByTitleAndAuthor(String title, String author);

    HttpStatus createNote(String title, String body, String author);

    boolean noteWithNameExists(String title);

    boolean userHasNote(String title, String username);

    String getRawNote(String title, String author);

    String getRawNote(String title, String author, boolean isAuthor);

    List<Note> getNotesByUser(String username);

    HttpStatus deleteNote(String title, String username);

    HttpStatus renameNote(String oldTitle, String newTitle, String username);

    int getNoteCount(String username);

    HttpStatus deleteNotesByAuthor(String author);

    List<String> getRawNotesByIdList(List<Long> noteIds);

    List<Note> getNotesByIdList(List<Long> noteIds);

    HttpStatus updateLastModified(String title, String author);

    HttpStatus updateLastViewedByAuthor(String title, String author);

    HttpStatus updateLastViewed(String title, String author);

    ResponseEntity<String> duplicateNote(String title, String author);

    Optional<Note> getNote(String title, String author);

    /**
     *
     * @param note note to copy from
     * @param username user to copy note to
     * @return http code and string containing the name of the new note
     */
    ResponseEntity<String> copyNoteToUser(Note note, String username);

    NoteIdentifier getNoteIdentifierById(long noteId) throws NotFoundException, SQLException;

}
