package com.zackmurry.nottteme.dao.share;

import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.models.Note;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public interface ShareDao {

    HttpStatus shareNoteWithUser(String author, String title, String recipient);

    List<String> getSharesOfNote(String username, String title);

    boolean noteIsSharedWithUser(String title, String author, String recipient);

    String getRawSharedNote(String title, String author, String username) throws NotFoundException, UnauthorizedException;

    HttpStatus unshareNoteWithUser(String username, String title, String recipient);

    List<Long> getNoteIdsSharedWithUser(String username);

    Optional<Note> getSharedNote(String title, String author, String username) throws UnauthorizedException;

}
