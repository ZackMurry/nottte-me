package com.zackmurry.nottteme.models;

public class GeneratedShortcut implements Shortcut {

    private String name;
    private CSSAttribute attribute;

    public GeneratedShortcut() {

    }

    public GeneratedShortcut(String name, CSSAttribute attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public CSSAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(CSSAttribute attribute) {
        this.attribute = attribute;
    }

}
