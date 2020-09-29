package com.zackmurry.nottteme.models;

public class NoteIdentifier {

    private long id;
    private String username;
    private String title;

    public NoteIdentifier() {

    }

    public NoteIdentifier(long id, String username, String title) {
        this.id = id;
        this.username = username;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
