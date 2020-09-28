package com.zackmurry.nottteme.dao.shortcuts;

import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.GeneratedShortcut;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface ShortcutDao {


    List<TextShortcut> getTextShortcutsByUsername(String username);

    List<TextShortcut> getTextShortcutsByUsernameOrderedByName(String username);

    HttpStatus addTextShortcut(String username, String name, String text, String key, boolean alt);

    HttpStatus deleteTextShortcutByName(String username, String shortcutName);

    HttpStatus updateTextShortcutByName(String username, String shortcutName, TextShortcut newTextShortcut);

    HttpStatus setTextShortcutsByName(String username, List<TextShortcut> updatedTextShortcuts);

    List<StyleShortcut> getStyleShortcutsByUsername(String username);

    HttpStatus addStyleShortcut(String username, String name, String key, List<CSSAttribute> attributes, boolean alt);

    HttpStatus setStyleShortcutsByName(String username, List<StyleShortcut> updatedStyleShortcuts);

    HttpStatus deleteStyleShortcutByName(String username, String shortcutName);

    HttpStatus updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut);

    List<StyleShortcut> getStyleShortcutsByUsernameOrderedByName(String username);

    HttpStatus addCSSAttributeToStyleShortcut(String username, String shortcutName, CSSAttribute attribute);

    HttpStatus removeCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName);

    CSSAttribute getCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName) throws NotFoundException;

    StyleShortcut getStyleShortcutByUsername(String username, String shortcutName) throws NotFoundException;

    List<CSSAttribute> getCSSAttributesFromStyleShortcut(String username, String shortcutName) throws NotFoundException;

    HttpStatus addSharedStyleShortcutsToUser(String username, List<StyleShortcut> newStyleShortcuts);

    List<StyleShortcut> getSharedStyleShortcutsByUser(String username);

    HttpStatus deleteStyleShortcutsByUser(String username);

    HttpStatus deleteSharedStyleShortcutsByUser(String username);

    HttpStatus deleteTextShortcutsByUser(String username);

    HttpStatus deleteGeneratedShortcutsByUser(String username);

    HttpStatus addGeneratedShortcut(String username, GeneratedShortcut generatedShortcut);

    List<GeneratedShortcut> getGeneratedShortcutsByUser(String username);

    HttpStatus setGeneratedShortcutsByUser(String username, List<GeneratedShortcut> generatedShortcuts);

}
