package com.zackmurry.nottteme.models;

import java.util.UUID;

public class LinkShare {

    private UUID id;
    private String author;
    private long noteId;
    private String authority;
    private String status;
    private int timesUsed;

    public LinkShare() {

    }

    public LinkShare(UUID id, String author, long noteId, ShareAuthority authority, LinkShareStatus status, int timesUsed) {
        this.id = id;
        this.author = author;
        this.noteId = noteId;
        this.authority = authority.getAuthority();
        this.status = status.getStatus();
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

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
