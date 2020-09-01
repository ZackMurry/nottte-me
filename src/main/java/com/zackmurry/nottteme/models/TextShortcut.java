package com.zackmurry.nottteme.models;

/**
 * used for user-defined keyboard shortcuts
 */
public class TextShortcut implements Shortcut {

    private String name;
    private String key;
    private String text;

    public TextShortcut() {

    }

    public TextShortcut(String name, String text, String key) {
        this.name = name;
        this.key = key;
        this.text = text;
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

}
