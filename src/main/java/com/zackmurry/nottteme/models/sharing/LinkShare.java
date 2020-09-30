package com.zackmurry.nottteme.models.sharing;

import java.util.UUID;

public class LinkShare {

    private UUID id;
    private String author;
    private long noteId;
    private ShareAuthority authority;
    private LinkShareStatus status;
    private int timesUsed;

    public LinkShare() {

    }

    public LinkShare(UUID id, String author, long noteId, ShareAuthority authority, LinkShareStatus status, int timesUsed) {
        this.id = id;
        this.author = author;
        this.noteId = noteId;
        this.authority = authority;
        this.status = status;
        this.timesUsed = timesUsed;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public ShareAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(ShareAuthority authority) {
        this.authority = authority;
    }

    public LinkShareStatus getStatus() {
        return status;
    }

    public void setStatus(LinkShareStatus status) {
        this.status = status;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }
}
