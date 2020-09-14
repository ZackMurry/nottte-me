package com.zackmurry.nottteme.models;

import java.util.List;

public class StyleShortcut implements Shortcut {

    private String name;
    private String key;
    private List<CSSAttribute> attributes;

    public StyleShortcut() {

    }

    public StyleShortcut(String name, String key, List<CSSAttribute> attributes) {
        this.name = name;
        this.key = key;
        this.attributes = attributes;
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

    public List<CSSAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<CSSAttribute> attributes) {
        this.attributes = attributes;
    }
}
