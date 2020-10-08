package com.zackmurry.nottteme.models.notes;

import java.util.List;

public class Block {

    private String key;
    private String text;
    private String type;
    private int depth;
    private List<InlineStyleRange> inlineStyleRanges;
    private List<String> entityRanges;
    private NoteDataObject data;

    public Block() {

    }

    public Block(String key, String text, String type, int depth, List<InlineStyleRange> inlineStyleRanges, List<String> entityRanges, NoteDataObject data) {
        this.key = key;
        this.text = text;
        this.type = type;
        this.depth = depth;
        this.inlineStyleRanges = inlineStyleRanges;
        this.entityRanges = entityRanges;
        this.data = data;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<InlineStyleRange> getInlineStyleRanges() {
        return inlineStyleRanges;
    }

    public void setInlineStyleRanges(List<InlineStyleRange> inlineStyleRanges) {
        this.inlineStyleRanges = inlineStyleRanges;
    }

    public List<String> getEntityRanges() {
        return entityRanges;
    }

    public void setEntityRanges(List<String> entityRanges) {
        this.entityRanges = entityRanges;
    }

    public NoteDataObject getData() {
        return data;
    }

    public void setData(NoteDataObject data) {
        this.data = data;
    }

}
