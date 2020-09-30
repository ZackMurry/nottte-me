package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.link_shares.LinkShareDao;
import com.zackmurry.nottteme.models.NoteIdentifier;
import com.zackmurry.nottteme.models.sharing.LinkShare;
import com.zackmurry.nottteme.models.sharing.LinkShareRequest;
import com.zackmurry.nottteme.models.sharing.LinkShareStatus;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
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
        NoteIdentifier noteIdentifier = noteService.getNoteIdentifierById(linkShare.getNoteId());
        if(!shareService.shareNoteWithUser(noteIdentifier.getId(), username).equals(HttpStatus.NOT_MODIFIED)) {
            linkShareDao.incrementShareableLinkUsages(id);
        }
        return noteIdentifier;
    }

    public HttpStatus disableSharableLink(UUID id, String username) {
        boolean userCreatedLink;
        try {
            userCreatedLink = linkShareDao.getAuthorById(id).equals(username);
        } catch (NotFoundException e) {
            return HttpStatus.NOT_FOUND;
        } catch (SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        if(!userCreatedLink) return HttpStatus.UNAUTHORIZED;
        return linkShareDao.setShareableLinkStatus(id, LinkShareStatus.DISABLED);
    }

    public List<LinkShare> getShareableLinksFromNote(String noteName, String username) {
        long noteId = noteService.getIdByTitleAndAuthor(noteName, username);
        return linkShareDao.getLinkSharesByNoteId(noteId);
    }

    public HttpStatus setStatusOfLinkSharesOfNote(String title, String username, LinkShareStatus newStatus) {
        long noteId = noteService.getIdByTitleAndAuthor(title, username);
        return linkShareDao.setStatusOfLinkSharesOfNote(noteId, newStatus);
    }

    public HttpStatus setStatusOfLinkSharesByUser(String username, LinkShareStatus newStatus) {
        return linkShareDao.setStatusOfLinkSharesByUser(username, newStatus);
    }

    public HttpStatus updateSharableLink(UUID id, LinkShare newLinkShare, String username) {
        if(newLinkShare.getStatus() == null || newLinkShare.getAuthority() == null) {
            return HttpStatus.NOT_ACCEPTABLE;
        }
        try {
            if(!linkShareDao.getAuthorById(id).equals(username)) {
                return HttpStatus.UNAUTHORIZED;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (NotFoundException e) {
            return HttpStatus.NOT_FOUND;
        }
        return linkShareDao.updateSharableLink(id, newLinkShare);
    }

    public NoteIdentifier getNoteIdentifierById(UUID id) throws NotFoundException, SQLException {
        LinkShare linkShare = linkShareDao.getLinkShareById(id);
        return noteService.getNoteIdentifierById(linkShare.getNoteId());
    }
}
