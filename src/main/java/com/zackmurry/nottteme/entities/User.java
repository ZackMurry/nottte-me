package com.zackmurry.nottteme.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zackmurry.nottteme.models.TextShortcut;
import com.zackmurry.nottteme.models.StyleShortcut;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
public class User {

    @Id
    @Column
    private String username;

    @Column
    private String password;

    @Column(name = "text_shortcuts")
    private String textShortcuts;

    @Column(name = "style_shortcuts")
    private String styleShortcuts;

    //todo might need to have private lists of shortcuts for each type, but i don't see a need for them atm

    private static final Gson gson = new Gson();
    private static final Type keyboardShortcutListType = new TypeToken<ArrayList<TextShortcut>>(){}.getType();
    private static final Type styleShortcutListType = new TypeToken<ArrayList<StyleShortcut>>(){}.getType();

    public User() {

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
