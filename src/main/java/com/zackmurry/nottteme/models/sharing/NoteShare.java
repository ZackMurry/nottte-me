package com.zackmurry.nottteme.models.sharing;

//todo add permissions for letting non-owners share notes
public class NoteShare {

    private long id;
    private long noteId;
    private String sharedUsername;

    public NoteShare() {

    }

    public NoteShare(long id, long noteId, String sharedUsername) {
        this.id = id;
        this.noteId = noteId;
        this.sharedUsername = sharedUsername;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public String getSharedUsername() {
        return sharedUsername;
    }

    public void setSharedUsername(String sharedUsername) {
        this.sharedUsername = sharedUsername;
    }
}
