package com.zackmurry.nottteme.models;

/**
 * used as a request body for creating posts with body
 */
public class CreateNoteWithBodyRequest {

    private String title;
    private String body;

    public CreateNoteWithBodyRequest(String title, String body) {
        this.title = title;
        this.body = body;
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
}
