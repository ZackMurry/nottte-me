package com.zackmurry.nottteme.models.notes;

public class NoteDataObject {

    //nullable
    private String data;

    public NoteDataObject() {

    }

    public NoteDataObject(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
