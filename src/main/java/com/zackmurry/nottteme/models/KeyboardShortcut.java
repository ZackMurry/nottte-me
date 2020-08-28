package com.zackmurry.nottteme.models;

/**
 * todo deleting and editing shortcuts
 */
public class KeyboardShortcut {

    private String name;
    private int keyCode;
    private String text;

    public KeyboardShortcut() {

    }

    public KeyboardShortcut(String name, String text, int keyCode) {
        this.name = name;
        this.keyCode = keyCode;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
