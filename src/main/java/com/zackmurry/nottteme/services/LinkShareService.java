package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.link_shares.LinkShareDao;
import com.zackmurry.nottteme.models.LinkShare;
import com.zackmurry.nottteme.models.LinkShareRequest;
import com.zackmurry.nottteme.models.NoteIdentifier;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.UUID;

@Service
public class LinkShareService {

    @Autowired
    private LinkShareDao linkShareDao;

    @Autowired
    private NoteService noteService;

    @Autowired
    private ShareService shareService;

    public HttpStatus createShareableLink(LinkShareRequest request) {
        return linkShareDao.createShareableLink(request);
    }


    public NoteIdentifier useShareableLink(UUID id, String username) throws NotFoundException, SQLException {
        LinkShare linkShare = linkShareDao.getLinkShareById(id);
        return noteService.getNoteIdentifierById(linkShare.getNoteId());
    }
}
