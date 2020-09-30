package com.zackmurry.nottteme.models.sharing;

public class LinkShareRequest {

    private String name;
    private String authority; //pretty useless right now, as there's only one permission
    private String author;

    public LinkShareRequest() {

    }

    public LinkShareRequest(String name, String authority) {
        this.name = name;
        this.authority = authority;
    }

    public LinkShareRequest(String name, String permission, String author) {
        this.name = name;
        this.authority = permission;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String noteName) {
        this.name = noteName;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
