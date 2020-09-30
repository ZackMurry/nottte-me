package com.zackmurry.nottteme.models.sharing;

public enum LinkShareStatus {

    ACTIVE("ACTIVE"),
    ACCOUNT_DELETED("ACCOUNT_DELETED"),
    NOTE_DELETED("NOTE_DELETED"),
    DISABLED("DISABLED");

    private final String status;

    LinkShareStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
