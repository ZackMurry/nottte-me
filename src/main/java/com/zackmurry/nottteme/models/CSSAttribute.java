package com.zackmurry.nottteme.models;

public final class CSSAttribute {

    private String attribute;
    private String value;

    public CSSAttribute() {

    }

    public CSSAttribute(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof CSSAttribute)) return false;
        CSSAttribute comparing = (CSSAttribute) obj;
        return attribute.equals(comparing.getAttribute()) && value.equals(comparing.getValue());
    }
}
