package com.zackmurry.nottteme.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zackmurry.nottteme.dao.notes.NoteDao;
import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.models.NoteIdentifier;
import com.zackmurry.nottteme.models.notes.*;
import com.zackmurry.nottteme.models.sharing.LinkShareStatus;
import com.zackmurry.nottteme.utils.NoteUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private LinkShareService linkShareService;

    private static final Gson gson = new Gson();

    public HttpStatus createNote(String title, String body, String author) {
        if(title.contains("/") || title.contains("%") || title.length() > 200) return HttpStatus.BAD_REQUEST;
        return noteDao.createNote(title, body, author);
    }

    public HttpStatus createNote(String title, String author) {
        return noteDao.createNote(title, NoteUtils.getBlankNoteBody(), author);
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

    public HttpStatus deleteNote(String title, String username) {
        linkShareService.setStatusOfLinkSharesOfNote(title, username, LinkShareStatus.NOTE_DELETED);
        return noteDao.deleteNote(title, username);
    }

    public HttpStatus renameNote(String oldTitle, String newTitle, String username) {
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

    public ResponseEntity<String> duplicateNote(String title, String username) {
        return noteDao.duplicateNote(title, username);
    }

    public ResponseEntity<String> copyNoteToUser(Note note, String username) {
        return noteDao.copyNoteToUser(note, username);
    }

    public Optional<Note> getNote(String title, String username) {
        return noteDao.getNote(title, username);
    }

    public NoteIdentifier getNoteIdentifierById(long noteId) throws NotFoundException, SQLException {
        return noteDao.getNoteIdentifierById(noteId);
    }

    public long getIdByTitleAndAuthor(String title, String author) {
        return noteDao.getIdByTitleAndAuthor(title, author);
    }

    public HttpStatus patchNote(String title, String author, RawNotePatch patch) {
        //get current note in database
        String current = noteDao.getRawNote(title, author);

        //converting string of note to object
        RawNoteContent content;
        try {
            content = NoteUtils.convertJSONNoteContentToObject(current);
        } catch (JsonSyntaxException e) {
            System.out.println("bad json: " + current);
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

        //applying changes described in patch
        int removedFromContent = 0; //how many blocks have been removed

        //if the blocks of the content don't exist exist, create a new ArrayList of them
        if(content.getBlocks() == null) {
            content.setBlocks(new ArrayList<>());
        }

        for (int i = 0; i < patch.getBlocks().size(); i++) {
            PatchBlock patchBlock = patch.getBlocks().get(i);
            Block contentBlock;

            //if this is a block appended to the end
            if(content.getBlocks().size() <= patchBlock.getIdx()-removedFromContent) {
                //add a new block to the blocks
                contentBlock = new Block();
                content.getBlocks().add(contentBlock);
            } else {
                //else get the block and alter it here
                contentBlock = content.getBlocks().get(patchBlock.getIdx()-removedFromContent);
            }
            if(patchBlock.getDeleted() != null && patchBlock.getDeleted()) {
                content.getBlocks().remove(patchBlock.getIdx()-removedFromContent++);
                continue;
            }
            if(patchBlock.getText() != null) {
                contentBlock.setText(patchBlock.getText());
            }
            if(patchBlock.getKey() != null) {
                contentBlock.setKey(patchBlock.getKey());
            }
            //data doesn't really get modified much
            if(patchBlock.getData() != null && patchBlock.getData().getData() != null) {
                contentBlock.setData(patchBlock.getData());
            }
            if(patchBlock.getDepth() != null) {
                contentBlock.setDepth(patchBlock.getDepth());
            }
            if(patchBlock.getType() != null) {
                contentBlock.setType(patchBlock.getType());
            }
            //entity ranges is untested (can't figure out when it is used)
            if(patchBlock.getEntityRanges() != null && patchBlock.getEntityRanges().size() > 0) {
                List<String> entityRanges = new ArrayList<>(patchBlock.getEntityRanges());
                contentBlock.setEntityRanges(entityRanges);
            }
            if(patchBlock.getInlineStyleRanges() != null) {
                //if the current block doesn't have an inlineStyleRange ArrayList, create one
                if (contentBlock.getInlineStyleRanges() == null) {
                    contentBlock.setInlineStyleRanges(new ArrayList<>());
                }

                int removedStyleRanges = 0; //number of removed style ranges
                for (int j = 0; j < patchBlock.getInlineStyleRanges().size(); j++) {
                    PatchInlineStyleRange patchRange = patchBlock.getInlineStyleRanges().get(j);

                    InlineStyleRange contentRange;

                    //if this a style appended to the end, create a new one
                    if(contentBlock.getInlineStyleRanges().size() <= patchRange.getIdx() - removedStyleRanges) {
                        contentRange = new InlineStyleRange();
                        contentBlock.getInlineStyleRanges().add(contentRange);
                    } else {
                        //else edit the existing one
                        contentRange = contentBlock.getInlineStyleRanges().get(patchRange.getIdx() - removedStyleRanges);
                    }
                    if(patchRange.getDeleted() != null && patchRange.getDeleted()) {
                        contentBlock.getInlineStyleRanges().remove(patchRange.getIdx() - removedStyleRanges++);
                        continue;
                    }
                    if(patchRange.getLength() != null) {
                        contentRange.setLength(patchRange.getLength());
                    }
                    if(patchRange.getOffset() != null) {
                        contentRange.setOffset(patchRange.getOffset());
                    }
                    if(patchRange.getStyle() != null) {
                        contentRange.setStyle(patchRange.getStyle());
                    }

                }
            }
        }
        return noteDao.updateNote(title, author, gson.toJson(content));
    }

}
