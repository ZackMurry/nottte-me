package com.zackmurry.nottteme.models;

/**
 * used by rest controller as a body for the POST /api/v1/notes/create request. just contains a string named title
 */
public class CreateNoteRequest {

    private String title;

    public CreateNoteRequest() {

    }

    public CreateNoteRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
