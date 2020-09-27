package com.zackmurry.nottteme.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @Column
    private long id;

    @Column
    private String author;

    @Column
    private String title;

    @Column
    private String body;

    @Column
    private Timestamp lastModified;

    @Column
    private Timestamp lastViewedByAuthor;

    @Column
    private Timestamp lastViewed;

    public Note() {

    }

    public Note(long id, String author, String title, String body, Timestamp lastModified, Timestamp lastViewedByAuthor, Timestamp lastViewed) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.body = body;
        this.lastModified = lastModified;
        this.lastViewedByAuthor = lastViewedByAuthor;
        this.lastViewed = lastViewed;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public Timestamp getLastViewedByAuthor() {
        return lastViewedByAuthor;
    }

    public void setLastViewedByAuthor(Timestamp lastViewedByAuthor) {
        this.lastViewedByAuthor = lastViewedByAuthor;
    }

    public Timestamp getLastViewed() {
        return lastViewed;
    }

    public void setLastViewed(Timestamp lastViewed) {
        this.lastViewed = lastViewed;
    }

    /**
     * ignores id and lastModified
     * @param obj comparing object
     * @return whether the compared fields are equal
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Note)) return false;
        Note comparing = (Note) obj;
        if(!this.title.equals(comparing.getTitle())) return false;
        if(!this.body.equals(comparing.getBody())) return false;
        return this.author.equals(comparing.getAuthor());
    }
}
