package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.user.UserDao;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public boolean createUserAccount(String username, String password) {
        return userDao.createUserAccount(username, password);
    }

    public boolean usernameExists(String username) {
        return userDao.accountExists(username);
    }

    public Optional<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    public HttpStatus addTextShortcut(String username, String name, String text, String key) {
        return userDao.addTextShortcut(username, name, text, key);
    }

    public List<TextShortcut> getTextShortcutsByUsername(String username) {
        return userDao.getTextShortcutsByUsername(username);
    }

    public List<TextShortcut> getTextShortcutsByUsernameOrderedByName(String username) {
        return userDao.getTextShortcutsByUsernameOrderedByName(username);
    }

    public HttpStatus deleteTextShortcutByName(String username, String shortcutName) {
        return userDao.deleteTextShortcutByName(username, shortcutName);
    }

    public HttpStatus updateTextShortcutByName(String username, String shortcutName, TextShortcut updatedTextShortcut) {
        return userDao.updateTextShortcutByName(username, shortcutName, updatedTextShortcut);
    }

    public List<StyleShortcut> getStyleShortcutsByUsername(String username) {
        return userDao.getStyleShortcutsByUsername(username);
    }

    public HttpStatus addStyleShortcut(String username, String name, String key, List<CSSAttribute> attributes) {
        return userDao.addStyleShortcut(username, name, key, attributes);
    }

    public HttpStatus deleteStyleShortcutByName(String username, String shortcutName) {
        return userDao.deleteStyleShortcutByName(username, shortcutName);
    }

    public HttpStatus updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut) {
        return userDao.updateStyleShortcutByName(username, shortcutName, updatedStyleShortcut);
    }

    public List<StyleShortcut> getStyleShortcutsByUsernameOrderedByName(String username) {
        return userDao.getStyleShortcutsByUsernameOrderedByName(username);
    }

    /**
     * deletes account from users table -- *notably* keeps notes
     * @param username username of account to delete
     * @return response status
     */
    public HttpStatus deleteAccount(String username) {
        return userDao.deleteAccount(username);
    }

    public HttpStatus addCSSAttributeToStyleShortcut(String username, String shortcutName, CSSAttribute attribute) {
        return userDao.addCSSAttributeToStyleShortcut(username, shortcutName, attribute);
    }

    public HttpStatus removeCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName) {
        return userDao.removeCSSAttributeFromStyleShortcut(username, shortcutName, attributeName);
    }

    public CSSAttribute getCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName) throws NotFoundException {
        return userDao.getCSSAttributeFromStyleShortcut(username, shortcutName, attributeName);
    }

    public StyleShortcut getStyleShortcutByUsername(String username, String shortcutName) throws NotFoundException {
        return userDao.getStyleShortcutByUsername(username, shortcutName);
    }

    public List<CSSAttribute> getCSSAttributesFromStyleShortcut(String username, String shortcutName) throws NotFoundException {
        return userDao.getCSSAttributesFromStyleShortcut(username, shortcutName);
    }
}
