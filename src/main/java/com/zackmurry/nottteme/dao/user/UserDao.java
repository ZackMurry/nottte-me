package com.zackmurry.nottteme.dao.user;

import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.KeyboardShortcut;
import com.zackmurry.nottteme.models.StyleShortcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    boolean createUserAccount(String username, String password);

    boolean accountExists(String username);

    Optional<User> getUserByUsername(String username);

    List<KeyboardShortcut> getKeyboardShortcutsByUsername(String username);

    List<KeyboardShortcut> getKeyboardShortcutsByUsernameOrderedByName(String username);

    ResponseEntity<HttpStatus> addKeyboardShortcut(String username, String name, String text, String keyCode);

    ResponseEntity<HttpStatus> deleteKeyboardShortcutByName(String username, String shortcutName);

    ResponseEntity<HttpStatus> updateKeyboardShortcutByName(String username, String shortcutName, KeyboardShortcut newKeyboardShortcut);

    ResponseEntity<HttpStatus> setKeyboardShortcutsByName(String username, List<KeyboardShortcut> updatedKeyboardShortcuts);


    List<StyleShortcut> getStyleShortcutsByUsername(String username);

    ResponseEntity<HttpStatus> addStyleShortcut(String username, String name, String key, String style, String value);

    ResponseEntity<HttpStatus> setStyleShortcutsByName(String username, List<StyleShortcut> updatedStyleShortcuts);

    ResponseEntity<HttpStatus> deleteStyleShortcutByName(String username, String shortcutName);

    ResponseEntity<HttpStatus> updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut);

}
