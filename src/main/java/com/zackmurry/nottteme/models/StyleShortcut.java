package com.zackmurry.nottteme.models;

public class StyleShortcut implements Shortcut {

    private String name;
    private String key;
    private String attribute; //css attribute
    private String value; //css value

    public StyleShortcut() {

    }

    public StyleShortcut(String name, String key, String attribute, String value) {
        this.name = name;
        this.key = key;
        this.attribute = attribute;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
}
