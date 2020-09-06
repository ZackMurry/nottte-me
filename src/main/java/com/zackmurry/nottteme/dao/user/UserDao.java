package com.zackmurry.nottteme.dao.user;

import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    boolean createUserAccount(String username, String password);

    boolean accountExists(String username);

    Optional<User> getUserByUsername(String username);

    List<TextShortcut> getTextShortcutsByUsername(String username);

    List<TextShortcut> getTextShortcutsByUsernameOrderedByName(String username);

    ResponseEntity<HttpStatus> addTextShortcut(String username, String name, String text, String keyCode);

    ResponseEntity<HttpStatus> deleteTextShortcutByName(String username, String shortcutName);

    ResponseEntity<HttpStatus> updateTextShortcutByName(String username, String shortcutName, TextShortcut newTextShortcut);

    ResponseEntity<HttpStatus> setTextShortcutsByName(String username, List<TextShortcut> updatedTextShortcuts);


    List<StyleShortcut> getStyleShortcutsByUsername(String username);

    ResponseEntity<HttpStatus> addStyleShortcut(String username, String name, String key, String style, String value);

    ResponseEntity<HttpStatus> setStyleShortcutsByName(String username, List<StyleShortcut> updatedStyleShortcuts);

    ResponseEntity<HttpStatus> deleteStyleShortcutByName(String username, String shortcutName);

    ResponseEntity<HttpStatus> updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut);

    List<StyleShortcut> getStyleShortcutsByUsernameOrderedByName(String username);

}
