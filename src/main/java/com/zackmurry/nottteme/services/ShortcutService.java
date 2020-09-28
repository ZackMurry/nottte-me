package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.shortcuts.ShortcutDao;
import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.GeneratedShortcut;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;
import com.zackmurry.nottteme.utils.ShortcutUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShortcutService {

    @Autowired
    private ShortcutDao shortcutDao;
    
    public HttpStatus addTextShortcut(String username, String name, String text, String key, boolean alt) {
        return shortcutDao.addTextShortcut(username, name, text, key, alt);
    }

    public List<TextShortcut> getTextShortcutsByUsername(String username) {
        return shortcutDao.getTextShortcutsByUsername(username);
    }

    public List<TextShortcut> getTextShortcutsByUsernameOrderedByName(String username) {
        return shortcutDao.getTextShortcutsByUsernameOrderedByName(username);
    }

    public HttpStatus deleteTextShortcutByName(String username, String shortcutName) {
        return shortcutDao.deleteTextShortcutByName(username, shortcutName);
    }

    public HttpStatus updateTextShortcutByName(String username, String shortcutName, TextShortcut updatedTextShortcut) {
        return shortcutDao.updateTextShortcutByName(username, shortcutName, updatedTextShortcut);
    }

    public List<StyleShortcut> getStyleShortcutsByUsername(String username) {
        return shortcutDao.getStyleShortcutsByUsername(username);
    }

    public HttpStatus addStyleShortcut(String username, String name, String key, List<CSSAttribute> attributes, boolean alt) {
        return shortcutDao.addStyleShortcut(username, name, key, attributes, alt);
    }

    public HttpStatus deleteStyleShortcutByName(String username, String shortcutName) {
        return shortcutDao.deleteStyleShortcutByName(username, shortcutName);
    }

    public HttpStatus updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut) {
        return shortcutDao.updateStyleShortcutByName(username, shortcutName, updatedStyleShortcut);
    }

    public List<StyleShortcut> getStyleShortcutsByUsernameOrderedByName(String username) {
        return shortcutDao.getStyleShortcutsByUsernameOrderedByName(username);
    }

    public HttpStatus addCSSAttributeToStyleShortcut(String username, String shortcutName, CSSAttribute attribute) {
        return shortcutDao.addCSSAttributeToStyleShortcut(username, shortcutName, attribute);
    }

    public HttpStatus removeCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName) {
        return shortcutDao.removeCSSAttributeFromStyleShortcut(username, shortcutName, attributeName);
    }

    public CSSAttribute getCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName) throws NotFoundException {
        return shortcutDao.getCSSAttributeFromStyleShortcut(username, shortcutName, attributeName);
    }

    public StyleShortcut getStyleShortcutByUsername(String username, String shortcutName) throws NotFoundException {
        return shortcutDao.getStyleShortcutByUsername(username, shortcutName);
    }

    public List<CSSAttribute> getCSSAttributesFromStyleShortcut(String username, String shortcutName) throws NotFoundException {
        return shortcutDao.getCSSAttributesFromStyleShortcut(username, shortcutName);
    }

    public HttpStatus addSharedStyleShortcutsToUser(String username, List<StyleShortcut> newStyleShortcuts) {
        return shortcutDao.addSharedStyleShortcutsToUser(username, newStyleShortcuts);
    }

    public List<StyleShortcut> getSharedStyleShortcutsByUser(String username) {
        return shortcutDao.getSharedStyleShortcutsByUser(username);
    }

    public HttpStatus deleteStyleShortcutsByUser(String username) {
        return shortcutDao.deleteStyleShortcutsByUser(username);
    }

    public HttpStatus deleteSharedStyleShortcutsByUser(String username) {
        return shortcutDao.deleteSharedStyleShortcutsByUser(username);
    }

    public HttpStatus deleteTextShortcutsByUser(String username) {
        return shortcutDao.deleteTextShortcutsByUser(username);
    }

    public List<GeneratedShortcut> getGeneratedShortcutsByUser(String username) {
        return shortcutDao.getGeneratedShortcutsByUser(username);
    }

    public boolean isUniqueStyledShortcutName(String username, String shortcutName) {
        List<StyleShortcut> styleShortcuts = shortcutDao.getStyleShortcutsByUsername(username);
        List<StyleShortcut> sharedStyleShortcuts = shortcutDao.getSharedStyleShortcutsByUser(username);
        List<GeneratedShortcut> generatedShortcuts = shortcutDao.getGeneratedShortcutsByUser(username);

        return styleShortcuts.stream().noneMatch(styleShortcut -> styleShortcut.getName().equals(shortcutName)) &&
                sharedStyleShortcuts.stream().noneMatch(sharedStyleShortcut -> sharedStyleShortcut.getName().equals(shortcutName)) &&
                generatedShortcuts.stream().noneMatch(generatedShortcut -> generatedShortcut.getName().equals(shortcutName));

    }

    public String addGeneratedShortcutToUser(String username, CSSAttribute cssAttribute) throws NotFoundException {
        List<GeneratedShortcut> generatedShortcuts = shortcutDao.getGeneratedShortcutsByUser(username);
        generatedShortcuts = generatedShortcuts.stream()
                .filter(generatedShortcut ->
                        generatedShortcut.getAttribute().getAttribute().equals(cssAttribute.getAttribute()) &&
                        generatedShortcut.getAttribute().getValue().equals(cssAttribute.getValue())
                )
                .collect(Collectors.toList());

        if(generatedShortcuts.size() == 0) {
            //create a new generated shortcut with a unique name
            int createIndex = 0;
            System.out.println(cssAttribute.getAttribute() + ", " + cssAttribute.getValue());
            String name = ShortcutUtils.anonymizeGeneratedShortcutName(username, cssAttribute, createIndex++);
            while(!isUniqueStyledShortcutName(username, name)) {
                name = ShortcutUtils.anonymizeGeneratedShortcutName(username, cssAttribute, createIndex++);
            }
            HttpStatus addStatus = shortcutDao.addGeneratedShortcut(username, new GeneratedShortcut(name, cssAttribute));
            if(addStatus.equals(HttpStatus.NOT_FOUND)) throw new NotFoundException("Cannot find user with name " + username + ".");
            return name;
        } else {
            //else just return the first matching already-generated shortcut
            return generatedShortcuts.get(0).getName();
        }

    }

}
