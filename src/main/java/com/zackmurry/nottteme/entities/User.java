package com.zackmurry.nottteme.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zackmurry.nottteme.models.KeyboardShortcut;
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

    @Column
    private String shortcuts;

    @Column(name = "style_shortcuts")
    private String styleShortcuts;

    @Transient
    private List<KeyboardShortcut> keyboardShortcuts;

    private static final Gson gson = new Gson();
    private static final Type keyboardShortcutListType = new TypeToken<ArrayList<KeyboardShortcut>>(){}.getType();
    private static final Type styleShortcutListType = new TypeToken<ArrayList<StyleShortcut>>(){}.getType();

    public User() {

    }

    public User(String username, String password, String keyboardShortcuts) {
        this.username = username;
        this.password = password;
        this.keyboardShortcuts = convertKeyboardShortcutStringToObjects(keyboardShortcuts);
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

    public List<KeyboardShortcut> getKeyboardShortcuts() {
        return keyboardShortcuts;
    }

    public void setKeyboardShortcuts(String keyboardShortcuts) {
        this.keyboardShortcuts = convertKeyboardShortcutStringToObjects(keyboardShortcuts);
    }

    /**
     * tool used for converting from JSON string to list of KeyboardShortcuts
     * @param keyboardShortcuts JSON string to parse
     * @return the list version of the JSON
     */
    public static List<KeyboardShortcut> convertKeyboardShortcutStringToObjects(String keyboardShortcuts) {
        return gson.fromJson(keyboardShortcuts, keyboardShortcutListType);
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
