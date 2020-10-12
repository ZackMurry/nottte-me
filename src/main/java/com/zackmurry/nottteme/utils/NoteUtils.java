package com.zackmurry.nottteme.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.zackmurry.nottteme.models.notes.RawNoteContent;
import com.zackmurry.nottteme.models.notes.RawNotePatch;

import java.lang.reflect.Type;


public class NoteUtils {

    private static final String BLANK_NOTE_BODY = "{\"entityMap\":{},\"blocks\":[{\"text\":\"\",\"key\":\"nottte\",\"type\":\"unstyled\",\"inlineStyleRanges\":[],\"entityRanges\":[]}]}";
    private static final Type RAW_NOTE_CONTENT_TYPE = new TypeToken<RawNoteContent>(){}.getType();
    private static final Type NOTE_PATCH_TYPE = new TypeToken<RawNotePatch>(){}.getType();

    private static final Gson gson = new Gson();

    public static String getBlankNoteBody() {
        return BLANK_NOTE_BODY;
    }

    public static RawNoteContent convertJSONNoteContentToObject(String json) throws JsonSyntaxException {
        return gson.fromJson(json, RAW_NOTE_CONTENT_TYPE);
    }

    public static RawNotePatch convertJSONPatchToObject(String json) throws JsonSyntaxException {
        return gson.fromJson(json, NOTE_PATCH_TYPE);
    }

}
