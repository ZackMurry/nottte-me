package com.zackmurry.nottteme.models.notes;

public class PatchInlineStyleRange {

    private Integer offset;
    private Integer length;
    private String style;
    private Boolean deleted;
    private int idx;

    public PatchInlineStyleRange() {

    }

    public PatchInlineStyleRange(int offset, int length, String style, Boolean deleted, int idx) {
        this.offset = offset;
        this.length = length;
        this.style = style;
        this.deleted = deleted;
        this.idx = idx;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Integer getLength() {
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

}
