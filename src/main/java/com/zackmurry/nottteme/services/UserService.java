package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.user.UserDao;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.KeyboardShortcut;
import com.zackmurry.nottteme.models.StyleShortcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<HttpStatus> addKeyboardShortcut(String username, String name, String text, String key) {
        return userDao.addKeyboardShortcut(username, name, text, key);
    }

    public List<KeyboardShortcut> getKeyboardShortcutsByUsername(String username) {
        return userDao.getKeyboardShortcutsByUsername(username);
    }

    public List<KeyboardShortcut> getKeyboardShortcutsByUsernameOrderedByName(String username) {
        return userDao.getKeyboardShortcutsByUsernameOrderedByName(username);
    }

    public ResponseEntity<HttpStatus> deleteKeyboardShortcutByName(String username, String shortcutName) {
        return userDao.deleteKeyboardShortcutByName(username, shortcutName);
    }

    public ResponseEntity<HttpStatus> updateKeyboardShortcutByName(String username, String shortcutName, KeyboardShortcut updatedKeyboardShortcut) {
        return userDao.updateKeyboardShortcutByName(username, shortcutName, updatedKeyboardShortcut);
    }

    public List<StyleShortcut> getStyleShortcutsByUsername(String username) {
        return userDao.getStyleShortcutsByUsername(username);
    }

    public ResponseEntity<HttpStatus> addStyleShortcut(String username, String name, String key, String attribute, String value) {
        return userDao.addStyleShortcut(username, name, key, attribute, value);
    }

    public ResponseEntity<HttpStatus> deleteStyleShortcutByName(String username, String shortcutName) {
        return userDao.deleteStyleShortcutByName(username, shortcutName);
    }

    public ResponseEntity<HttpStatus> updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut) {
        return userDao.updateStyleShortcutByName(username, shortcutName, updatedStyleShortcut);
    }
}
