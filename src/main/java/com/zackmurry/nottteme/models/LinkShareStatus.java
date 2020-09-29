package com.zackmurry.nottteme.models;

public enum LinkShareStatus {

    ACTIVE("active"),
    ACCOUNT_DELETED("account_deleted"),
    NOTE_DELETED("note_deleted"),
    DISABLED("disabled");

    private final String status;

    LinkShareStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
