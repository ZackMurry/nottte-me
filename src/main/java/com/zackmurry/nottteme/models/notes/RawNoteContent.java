package com.zackmurry.nottteme.models.notes;

import java.util.List;

public class RawNoteContent {

    private List<Block> blocks;
    private EntityMap entityMap;

    public RawNoteContent() {

    }

    public RawNoteContent(List<Block> blocks, EntityMap entityMap) {
        this.blocks = blocks;
        this.entityMap = entityMap;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public EntityMap getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(EntityMap entityMap) {
        this.entityMap = entityMap;
    }

}
