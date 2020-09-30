package com.zackmurry.nottteme.models;

public class NoteIdentifier {

    private long id;
    private String author;
    private String title;

    public NoteIdentifier() {

    }

    public NoteIdentifier(long id, String username, String title) {
        this.id = id;
        this.author = username;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
