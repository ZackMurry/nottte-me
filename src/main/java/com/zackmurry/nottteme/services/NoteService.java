package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.notes.NoteDao;
import com.zackmurry.nottteme.models.Note;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteDao noteDao;

    public HttpStatus createNote(String title, String body, String author) {
        return noteDao.createNote(title, body, author);
    }

    public HttpStatus saveNote(String title, String author, String body) {
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

    public List<Note> getNotesByUser(String username) {
        return noteDao.getNotesByUser(username);
    }

    public HttpStatus deleteNote(String title, String username) throws NotFoundException {
        return noteDao.deleteNote(title, username);
    }

    public HttpStatus renameNote(String oldTitle, String newTitle, String username) throws NotFoundException {
        return noteDao.renameNote(oldTitle, newTitle, username);
    }

    public int getNoteCount(String username) {
        return noteDao.getNoteCount(username);
    }

    public HttpStatus deleteNotesByAuthor(String author) {
        return noteDao.deleteNotesByAuthor(author);
    }

    public List<String> getRawNotesByIdList(List<Long> noteIds) {
        return noteDao.getRawNotesByIdList(noteIds);
    }

    public List<Note> getNotesByIdList(List<Long> noteIds) {
        return noteDao.getNotesByIdList(noteIds);
    }

    public HttpStatus duplicateNote(String title, String username) {
        return noteDao.duplicateNote(title, username);
    }
}
