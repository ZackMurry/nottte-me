package com.zackmurry.nottteme.dao.share;

import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface ShareDao {

    HttpStatus shareNoteWithUser(String author, String title, String recipient);

    List<String> getSharesOfNote(String username, String title) throws NotFoundException;

    boolean noteIsSharedWithUser(String title, String author, String recipient);

    String getSharedNote(String title, String author, String username) throws NotFoundException, UnauthorizedException;

    HttpStatus unshareNoteWithUser(String username, String title, String recipient) throws NotFoundException;

}
