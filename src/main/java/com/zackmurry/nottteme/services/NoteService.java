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

    public HttpStatus duplicateNote(String title, String username) {
        return noteDao.duplicateNote(title, username);
    }

    public HttpStatus copyNoteToUser(Note note, String username) {
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
        String current = noteDao.getRawNote(title, author);
        RawNoteContent content;
        try {
            content = NoteUtils.convertJSONNoteContentToObject(current);
        } catch (JsonSyntaxException e) {
            System.out.println("bad json: " + current);
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

        int removedFromContent = 0;
        for (int i = 0; i < patch.getBlocks().size(); i++) {
            PatchBlock patchBlock = patch.getBlocks().get(i);
            Block contentBlock;

            if(content.getBlocks() == null) {
                content.setBlocks(new ArrayList<>());
            }
            //todo might not need -removedFromContent here
            if(content.getBlocks().size() <= patchBlock.getIdx()-removedFromContent) {
                contentBlock = new Block();
                List<Block> currentContentBlocks = content.getBlocks();
                currentContentBlocks.add(contentBlock);
                content.setBlocks(currentContentBlocks);
            } else {
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
                int removedStyleRanges = 0;
                for (int j = 0; j < patchBlock.getInlineStyleRanges().size(); j++) {
                    PatchInlineStyleRange patchRange = patchBlock.getInlineStyleRanges().get(j);

                    InlineStyleRange contentRange;
                    if (contentBlock.getInlineStyleRanges() == null) {
                        contentBlock.setInlineStyleRanges(new ArrayList<>());
                    }
                    if(contentBlock.getInlineStyleRanges().size() <= patchRange.getIdx()) {
                        contentRange = new InlineStyleRange();
                        List<InlineStyleRange> ranges = contentBlock.getInlineStyleRanges();
                        ranges.add(contentRange);
                        contentBlock.setInlineStyleRanges(ranges); //might not need
                    } else {
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
