package com.zackmurry.nottteme.models.notes;

import java.util.List;

public class PatchBlock {

    private int idx;
    private Boolean deleted;
    private String key;
    private String text;
    private String type;
    private Integer depth;
    private List<PatchInlineStyleRange> inlineStyleRanges;
    private List<String> entityRanges;
    private NoteDataObject data;

    public PatchBlock() {

    }

    public PatchBlock(int idx, String key, String text, String type, Integer depth, List<PatchInlineStyleRange> inlineStyleRanges, List<String> entityRanges, NoteDataObject data, Boolean deleted) {
        this.idx = idx;
        this.key = key;
        this.text = text;
        this.type = type;
        this.depth = depth;
        this.inlineStyleRanges = inlineStyleRanges;
        this.entityRanges = entityRanges;
        this.data = data;
        this.deleted = deleted;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
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

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public List<PatchInlineStyleRange> getInlineStyleRanges() {
        return inlineStyleRanges;
    }

    public void setInlineStyleRanges(List<PatchInlineStyleRange> inlineStyleRanges) {
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
