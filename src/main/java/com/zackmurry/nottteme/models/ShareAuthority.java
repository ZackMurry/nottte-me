package com.zackmurry.nottteme.models;

public enum ShareAuthority {

    VIEW("view"),
    EDIT("edit");

    private final String authority;

    ShareAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
