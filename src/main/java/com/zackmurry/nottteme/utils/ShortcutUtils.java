package com.zackmurry.nottteme.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShortcutUtils {

    private static final Gson gson = new Gson();
    private static final Type keyboardShortcutListType = new TypeToken<ArrayList<TextShortcut>>(){}.getType();
    private static final Type styleShortcutListType = new TypeToken<ArrayList<StyleShortcut>>(){}.getType();

    /**
     * tool used for converting from JSON string to list of KeyboardShortcuts
     * @param textShortcuts JSON string to parse
     * @return the list version of the JSON
     */
    public static List<TextShortcut> convertTextShortcutStringToObjects(String textShortcuts) {
        return gson.fromJson(textShortcuts, keyboardShortcutListType);
    }

    /**
     * tool used for converting from JSON string to list of StyleShortcuts
     * @param styleShortcuts JSON string to parse
     * @return the list version of the JSON
     */
    public static List<StyleShortcut> convertStyleShortcutStringToObjects(String styleShortcuts) {
        return gson.fromJson(styleShortcuts, styleShortcutListType);
    }


}
