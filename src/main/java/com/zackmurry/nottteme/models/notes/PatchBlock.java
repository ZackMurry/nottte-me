package com.zackmurry.nottteme.models.notes;

import java.util.List;

public class PatchBlock {

    private int idx;
    private String key;
    private String text;
    private String type;
    private Integer depth;
    private List<InlineStyleRange> inlineStyleRanges;
    private List<String> entityRanges;
    private List<String> data;

    public PatchBlock() {

    }

    public PatchBlock(int idx, String key, String text, String type, Integer depth, List<InlineStyleRange> inlineStyleRanges, List<String> entityRanges, List<String> data) {
        this.idx = idx;
        this.key = key;
        this.text = text;
        this.type = type;
        this.depth = depth;
        this.inlineStyleRanges = inlineStyleRanges;
        this.entityRanges = entityRanges;
        this.data = data;
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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
