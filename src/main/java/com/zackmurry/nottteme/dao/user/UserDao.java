package com.zackmurry.nottteme.dao.user;

import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    boolean createUserAccount(String username, String password);

    boolean accountExists(String username);

    Optional<User> getUserByUsername(String username);

    List<TextShortcut> getTextShortcutsByUsername(String username);

    List<TextShortcut> getTextShortcutsByUsernameOrderedByName(String username);

    HttpStatus addTextShortcut(String username, String name, String text, String key, boolean alt);

    HttpStatus deleteTextShortcutByName(String username, String shortcutName);

    HttpStatus updateTextShortcutByName(String username, String shortcutName, TextShortcut newTextShortcut);

    HttpStatus setTextShortcutsByName(String username, List<TextShortcut> updatedTextShortcuts);

    List<StyleShortcut> getStyleShortcutsByUsername(String username);

    HttpStatus addStyleShortcut(String username, String name, String key, List<CSSAttribute> attributes);

    HttpStatus setStyleShortcutsByName(String username, List<StyleShortcut> updatedStyleShortcuts);

    HttpStatus deleteStyleShortcutByName(String username, String shortcutName);

    HttpStatus updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut);

    List<StyleShortcut> getStyleShortcutsByUsernameOrderedByName(String username);

    HttpStatus deleteAccount(String username);

    HttpStatus addCSSAttributeToStyleShortcut(String username, String shortcutName, CSSAttribute attribute);

    HttpStatus removeCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName);

    CSSAttribute getCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName) throws NotFoundException;

    StyleShortcut getStyleShortcutByUsername(String username, String shortcutName) throws NotFoundException;

    List<CSSAttribute> getCSSAttributesFromStyleShortcut(String username, String shortcutName) throws NotFoundException;

}
