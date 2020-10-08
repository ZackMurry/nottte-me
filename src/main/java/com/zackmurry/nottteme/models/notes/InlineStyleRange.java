package com.zackmurry.nottteme.models.notes;

public class InlineStyleRange {

    private int offset;
    private int length;
    private String style;

    public InlineStyleRange() {

    }

    public InlineStyleRange(int offset, int length, String style) {
        this.offset = offset;
        this.length = length;
        this.style = style;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

}
