package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.share.ShareDao;
import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.models.sharing.NoteShare;
import com.zackmurry.nottteme.models.shortcuts.StyleShortcut;
import com.zackmurry.nottteme.utils.ShortcutUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public HttpStatus shareNoteWithUser(long id, String recipient) {
        return shareDao.shareNoteWithUser(id, recipient);
    }

    public List<NoteShare> getSharesOfNote(String username, String title) {
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

    public ResponseEntity<String> duplicateSharedNote(String author, String title, String username) {
        if(!shareDao.noteIsSharedWithUser(title, author, username)) return new ResponseEntity<>("Error", HttpStatus.UNAUTHORIZED);

        //copying style shortcuts from author to user
        List<StyleShortcut> authorStyleShortcuts = shortcutService.getStyleShortcutsByUsername(author);
        List<String> namesOfAuthorStyleShortcuts = authorStyleShortcuts.stream().map(StyleShortcut::getName).collect(Collectors.toList());
        List<StyleShortcut> userStyleShortcuts = shortcutService.getStyleShortcutsByUsername(username);
        userStyleShortcuts.addAll(shortcutService.getSharedStyleShortcutsByUser(username));

        userStyleShortcuts.addAll(shortcutService.getGeneratedShortcutsByUser(username).stream().map(shortcut -> {
            //convert to dummy style shortcut
            ArrayList<CSSAttribute> cssAttributes = new ArrayList<>();
            cssAttributes.add(shortcut.getAttribute());
            return new StyleShortcut(shortcut.getName(), "", cssAttributes, false);
        }).collect(Collectors.toList()));

        List<String> newNamesOfAuthorStyleShortcuts = ShortcutUtils.anonymizeStyleShortcuts(authorStyleShortcuts, userStyleShortcuts, author);
        for (int i = 0; i < authorStyleShortcuts.size(); i++) {
            authorStyleShortcuts.get(i).setName(newNamesOfAuthorStyleShortcuts.get(i));
        }

        HttpStatus addShortcutStatus = shortcutService.addSharedStyleShortcutsToUser(
                username,
                //in anonymizeStyleShortcuts(), copied style shortcuts that should be mapped to an already existing one are
                //given the same name as the pre-existing one. that's why we need to remove any style shortcuts that meet this,
                //as they already exist and don't need to be added
                authorStyleShortcuts.stream()
                        .filter(styleShortcut -> userStyleShortcuts.stream()
                                .noneMatch(s -> s.getName().equals(styleShortcut.getName()))
                        )
                        .collect(Collectors.toList())
        );

        //the two things that we don't want this to be are 400 and 404 (only error codes that this method returns),
        //so >= 400 achieves the same goal
        if(addShortcutStatus.value() >= 400) return new ResponseEntity<>("Error", addShortcutStatus);

        //duplicating note
        Optional<Note> optionalNote;
        try {
            optionalNote = getSharedNote(title, author, username);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error", HttpStatus.UNAUTHORIZED);
        }
        if(optionalNote.isEmpty()) return new ResponseEntity<>("Error", HttpStatus.UNAUTHORIZED);
        Note note = optionalNote.get();

        String body = note.getBody();
        for (int i = 0; i < namesOfAuthorStyleShortcuts.size(); i++) {
            String lookingFor = ",\"style\":\"" + namesOfAuthorStyleShortcuts.get(i) + "\"";
            String replacingWith = ",\"style\":\"" + newNamesOfAuthorStyleShortcuts.get(i) + "\"";
            body = body.replace(lookingFor, replacingWith);
        }
        note.setBody(body);

        return noteService.copyNoteToUser(note, username);
    }

    public Optional<NoteShare> getShareOfNote(String title, String author, String sharedUsername) {
        return shareDao.getShareOfNote(title, author, sharedUsername);
    }

    public HttpStatus setUserCanShareNote(String title, String author, String sharedUsername, boolean newValue) {
        if(!noteIsSharedWithUser(title, author, sharedUsername)) {
            return HttpStatus.PRECONDITION_FAILED;
        }
        Optional<NoteShare> optionalNoteShare = getShareOfNote(title, author, sharedUsername);
        if(optionalNoteShare.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        NoteShare noteShare = optionalNoteShare.get();
        if(noteShare.getCanShare() == newValue) {
            return HttpStatus.NOT_MODIFIED;
        }
        return shareDao.setUserCanShareNote(title, author, sharedUsername, newValue);
    }
}
