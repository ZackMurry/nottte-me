package com.zackmurry.nottteme.models.shortcuts;

import com.zackmurry.nottteme.models.CSSAttribute;

import java.util.List;

public class StyleShortcut implements BoundShortcut {

    private String name;
    private String key;
    private List<CSSAttribute> attributes;
    private boolean alt;

    public StyleShortcut() {

    }

    public StyleShortcut(String name, String key, List<CSSAttribute> attributes, boolean alt) {
        this.name = name;
        this.key = key;
        this.attributes = attributes;
        this.alt = alt;
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

    public boolean getAlt() {
        return alt;
    }

    public void setAlt(boolean alt) {
        this.alt = alt;
    }
}
