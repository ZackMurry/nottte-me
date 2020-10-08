package com.zackmurry.nottteme.models.notes;

import java.util.List;

public class RawNotePatch {

    private List<PatchBlock> blocks;


    public RawNotePatch() {

    }

    public RawNotePatch(List<PatchBlock> blocks) {
        this.blocks = blocks;
    }

    public List<PatchBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<PatchBlock> blocks) {
        this.blocks = blocks;
    }

}
