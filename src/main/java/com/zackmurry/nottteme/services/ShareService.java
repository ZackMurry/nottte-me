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
import java.util.Optional;
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

    public List<String> getSharesOfNote(String username, String title) {
        return shareDao.getSharesOfNote(username, title);
    }

    public boolean noteIsSharedWithUser(String title, String author, String username) {
        return shareDao.noteIsSharedWithUser(title, author, username);
    }

    public String getRawSharedNote(String title, String author, String username) throws NotFoundException, UnauthorizedException {
        return shareDao.getRawSharedNote(title, author, username);
    }

    public HttpStatus unshareNoteWithUser(String username, String title, String recipient) {
        return shareDao.unshareNoteWithUser(username, title, recipient);
    }

    public List<Long> getNoteIdsSharedWithUser(String username) {
        return shareDao.getNoteIdsSharedWithUser(username);
    }

    public Optional<Note> getSharedNote(String title, String author, String username) throws UnauthorizedException {
        return shareDao.getSharedNote(title, author, username);
    }

    public HttpStatus duplicateSharedNote(String author, String title, String username) {
        if(!shareDao.noteIsSharedWithUser(title, author, username)) return HttpStatus.UNAUTHORIZED;

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
        Optional<Note> optionalNote;
        try {
            optionalNote = getSharedNote(title, author, username);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpStatus.UNAUTHORIZED;
        }
        if(optionalNote.isEmpty()) return HttpStatus.UNAUTHORIZED;
        Note note = optionalNote.get();

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
