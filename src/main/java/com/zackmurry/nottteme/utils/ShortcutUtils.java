package com.zackmurry.nottteme.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zackmurry.nottteme.models.CSSAttribute;
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
     * anonymizes and removes conflicts that occur when merging two users' style shortcuts.
     *
     * text shortcuts are not included in input because these are only applied to a style map,
     * so they aren't looped through in the same breath as the text shortcuts
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
            //number that we have to append to the end of a shortcut name so that they're unique
            //i refuse to believe that i need to make this a long for a really weird user
            int appendToNameIndex = 0;

            //name to assign to this new shortcut
            String anonymizedName;

            mid: while(true) {

                //anonymized name that will be tested for conflicts
                //increments appendToNameIndex so that during the next (possible) loop,
                //the index at the end is increased and the checking process will start again with that
                final String tempAnonymizedName = anonymizeStyleShortcut(shortcut, author, appendToNameIndex++);

                //if this is the only style shortcut with this name
                if(baseStyleShortcuts.stream().noneMatch(baseShortcut -> baseShortcut.getName().equals(tempAnonymizedName)) ) {
                    //create a new anonymized name that won't be used in lambdas,
                    //as lambdas need effectively final values
                    String newAnonymizedName = tempAnonymizedName;

                    inner: while(listOutput.size() > 0) {
                        for (String outputShortcutName : listOutput) {
                            //if any of the newly added shortcuts have the same name as this,
                            //increment the index and try again
                            if (outputShortcutName.equals(newAnonymizedName)) {
                                newAnonymizedName = anonymizeStyleShortcut(shortcut, author, appendToNameIndex++);
                                continue inner;
                            }
                        }
                        //if we haven't continued inner yet, that means it's passed the check,
                        //so we can move to the next check
                        break;
                    }

                    //checking for identical shortcuts within baseStyleShortcuts that
                    //we can assign this shortcut to
                    outerFor: for (StyleShortcut styleShortcut : baseStyleShortcuts) {
                        if(shortcut.getAttributes().size() != styleShortcut.getAttributes().size()) continue;
                        for (CSSAttribute cssAttribute : shortcut.getAttributes()) {
                            //if there aren't any style shortcuts that have the same attributes,
                            //then continue to the next styleShortcut to check against
                            if(styleShortcut
                                    .getAttributes()
                                    .stream()
                                    .noneMatch(attribute ->
                                            attribute.getAttribute().equals(cssAttribute.getAttribute()) && attribute.getValue().equals(cssAttribute.getValue())
                                    )
                            ) {
                                continue outerFor;
                            }
                        }
                        listOutput.add(styleShortcut.getName());
                        continue outer;
                    }

                    //set the name to output as the newAnonymizedName
                    //and break, which will add it to the output
                    anonymizedName = newAnonymizedName;
                } else {
                    //if there's a conflicting base style shortcut with the same name check if it as the same attributes as this one.
                    //if it does, we're good; we can just assign this added one to the name of the conflicting one.
                    //if not, add one to the index and check if that passes the checks

                    Optional<StyleShortcut> optionalStyleShortcut = baseStyleShortcuts.stream()
                            .filter(styleShortcut -> styleShortcut.getName().equals(tempAnonymizedName))
                            .findFirst();

                    //this should never realistically be empty, but it's safer to check
                    if(optionalStyleShortcut.isEmpty()) continue;

                    StyleShortcut styleShortcut = optionalStyleShortcut.get();
                    if(styleShortcut.getAttributes().size() != shortcut.getAttributes().size()) continue;
                    for(int i = 0; i < styleShortcut.getAttributes().size(); i++) {
                        if(!styleShortcut.getAttributes().get(i).equals(shortcut.getAttributes().get(i))) continue mid;
                    }
                    //if this style shortcut matches, then just rename the current one to the existing one
                    anonymizedName = styleShortcut.getName();
                }
                //if all of the checks have passed, break, which will also add it to the output
                break;
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
        return author + "-" + styleShortcut.getAttributes().get(0).getAttribute() + "-" + styleShortcut.getAttributes().get(0).getValue().replace(" ", "") + (offset > 0 ? ("-" + offset) : "");
    }

}
