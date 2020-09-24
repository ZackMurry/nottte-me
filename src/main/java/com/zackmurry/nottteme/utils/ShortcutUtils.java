package com.zackmurry.nottteme.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /**
     * anonymizes and removes conflicts that occur when merging two users' style shortcuts
     *
     * @param addedStyleShortcuts style shortcuts to be added
     * @param baseStyleShortcuts shortcuts to add to (used to ensure no conflicts) (must include shared_style_shortcuts and style_shortcuts)
     * @param author author of addedStyleShortcuts (used for anonymizing)
     * @return an n-length list of strings, where n is the number of added style shortcuts.
     * output.get(i) is the new name of the style shortcut whose old name was found at addedStyleShortcuts.get(i)
     * only names in the addedStyleShortcuts will be modified in this manner
     */
    public static List<String> anonymizeStyleShortcuts(List<StyleShortcut> addedStyleShortcuts, List<StyleShortcut> baseStyleShortcuts, String author) {
        List<String> listOutput = new ArrayList<>();

        outer: for(StyleShortcut shortcut : addedStyleShortcuts) {
            int index = 0; //i refuse to believe that i need to make this a long for a really weird user
            String anonymizedName = "";
            mid: while(true) {
                final String tempAnonymizedName = anonymizeStyleShortcut(shortcut, author, index++);
                if(baseStyleShortcuts.stream().noneMatch(baseShortcut -> baseShortcut.getName().equals(tempAnonymizedName))) {
                    anonymizedName = tempAnonymizedName;
                    break;
                } else {
                    Optional<StyleShortcut> optionalStyleShortcut = baseStyleShortcuts.stream().filter(styleShortcut -> styleShortcut.getName().equals(tempAnonymizedName)).findFirst();
                    if(optionalStyleShortcut.isEmpty()) continue;
                    StyleShortcut styleShortcut = optionalStyleShortcut.get();
                    if(styleShortcut.getAttributes().size() != shortcut.getAttributes().size()) continue;
                    for(int i = 0; i < styleShortcut.getAttributes().size(); i++) {
                        if(!styleShortcut.getAttributes().get(i).equals(shortcut.getAttributes().get(i))) continue mid;
                    }
                    //if this style shortcut matches, then just rename the current one to the existing one
                    listOutput.add(styleShortcut.getName());
                    continue outer;
                }
            }
            listOutput.add(anonymizedName);
        }
        return listOutput;
    }

    public static String anonymizeStyleShortcut(StyleShortcut styleShortcut, String author) {
        return anonymizeStyleShortcut(styleShortcut, author, 0);
    }

    /**
     * internal method for looping through options of anonymized style shortcuts
     *
     * why anonymize? some people make really weird names for stuff (myself included),
     * so it'd be better to just anonymize (by anonymize i mean remove connection to the
     * original name, not the author), especially since we have to remove duplicate names anyways
     *
     * @param styleShortcut shortcut to anonymize
     * @param author author of style shortcut
     * @param offset number for adding characters at the end of variables for unique naming
     * @return new name of style shortcut
     */
    private static String anonymizeStyleShortcut(StyleShortcut styleShortcut, String author, int offset) {
        return author + '-' + styleShortcut.getAttributes().get(0).getAttribute() + '-' + styleShortcut.getAttributes().get(0).getValue().replace(" ", "") + (offset > 0 ? '-' + offset : "");
    }

}
