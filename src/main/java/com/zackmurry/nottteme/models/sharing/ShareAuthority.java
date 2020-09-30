package com.zackmurry.nottteme.models.sharing;

public enum ShareAuthority {

    VIEW("VIEW"),
    EDIT("EDIT");

    private final String authority;

    ShareAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
