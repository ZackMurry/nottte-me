package com.zackmurry.nottteme.models.shortcuts;

/**
 * used for user-defined keyboard shortcuts
 */
public class TextShortcut implements BoundShortcut {

    private String name;
    private String key;
    private String text;
    private boolean alt;

    public TextShortcut() {

    }

    public TextShortcut(String name, String text, String key, boolean alt) {
        this.name = name;
        this.key = key;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getAlt() {
        return alt;
    }

    public void setAlt(boolean alt) {
        this.alt = alt;
    }
}
