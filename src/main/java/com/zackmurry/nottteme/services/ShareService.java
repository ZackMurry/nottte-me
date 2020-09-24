package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.share.ShareDao;
import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.utils.ShortcutUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareService {

    @Autowired
    private ShareDao shareDao;

    @Autowired
    private ShortcutService shortcutService;

    @Autowired
    private NoteService noteService;

    public HttpStatus shareNoteWithUser(String author, String title, String recipient) {
        return shareDao.shareNoteWithUser(author, title, recipient);
    }

    public List<String> getSharesOfNote(String username, String title) throws NotFoundException {
        return shareDao.getSharesOfNote(username, title);
    }

    public boolean noteIsSharedWithUser(String title, String author, String username) {
        return shareDao.noteIsSharedWithUser(title, author, username);
    }

    public String getRawSharedNote(String title, String author, String username) throws NotFoundException, UnauthorizedException {
        return shareDao.getRawSharedNote(title, author, username);
    }

    public HttpStatus unshareNoteWithUser(String username, String title, String recipient) throws NotFoundException {
        return shareDao.unshareNoteWithUser(username, title, recipient);
    }

    public List<Long> getNoteIdsSharedWithUser(String username) {
        return shareDao.getNoteIdsSharedWithUser(username);
    }

    public Note getSharedNote(String title, String author, String username) throws UnauthorizedException, NotFoundException {
        return shareDao.getSharedNote(title, author, username);
    }

    public HttpStatus duplicateSharedNote(String author, String title, String username) throws UnauthorizedException, NotFoundException {
        if(!shareDao.noteIsSharedWithUser(title, author, username)) throw new UnauthorizedException("User does not have access to note.");

        //copying style shortcuts from author to user
        List<StyleShortcut> authorStyleShortcuts = shortcutService.getStyleShortcutsByUsername(author);
        List<String> namesOfAuthorStyleShortcuts = authorStyleShortcuts.stream().map(StyleShortcut::getName).collect(Collectors.toList());
        List<StyleShortcut> userStyleShortcuts = shortcutService.getStyleShortcutsByUsername(username);
        userStyleShortcuts.addAll(shortcutService.getSharedStyleShortcutsByUser(username));
        List<String> newNamesOfAuthorStyleShortcuts = ShortcutUtils.anonymizeStyleShortcuts(authorStyleShortcuts, userStyleShortcuts, author);
        for (int i = 0; i < authorStyleShortcuts.size(); i++) {
            authorStyleShortcuts.get(i).setName(newNamesOfAuthorStyleShortcuts.get(i));
        }

        HttpStatus addShortcutStatus = shortcutService.addSharedStyleShortcutsToUser(username, authorStyleShortcuts);
        if(addShortcutStatus.value() >= 400) return addShortcutStatus;

        //duplicating note
        Note note = getSharedNote(title, author, username);

        String body = note.getBody();
        for (int i = 0; i < namesOfAuthorStyleShortcuts.size(); i++) {
            String lookingFor = ",\"style\":\"" + namesOfAuthorStyleShortcuts.get(i) + "\"";
            String replacingWith = ",\"style\":\"" + newNamesOfAuthorStyleShortcuts.get(i) + "\"";
            //todo do this more efficiently
            body = body.replace(lookingFor, replacingWith);
        }
        note.setBody(body);

        return noteService.copyNoteToUser(note, username);
    }

}
