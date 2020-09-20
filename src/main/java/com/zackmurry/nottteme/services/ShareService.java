package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.share.ShareDao;
import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShareService {

    @Autowired
    private ShareDao shareDao;

    public HttpStatus shareNoteWithUser(String author, String title, String recipient) {
        return shareDao.shareNoteWithUser(author, title, recipient);
    }

    public List<String> getSharesOfNote(String username, String title) throws NotFoundException {
        return shareDao.getSharesOfNote(username, title);
    }

    public boolean noteIsSharedWithUser(String title, String author, String username) {
        return shareDao.noteIsSharedWithUser(title, author, username);
    }

    public String getSharedNote(String title, String author, String username) throws NotFoundException, UnauthorizedException {
        return shareDao.getSharedNote(title, author, username);
    }

    public HttpStatus unshareNoteWithUser(String username, String title, String recipient) throws NotFoundException {
        return shareDao.unshareNoteWithUser(username, title, recipient);
    }
}
