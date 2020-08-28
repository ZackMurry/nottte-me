package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.user.UserDao;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.KeyboardShortcut;
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

    public ResponseEntity<HttpStatus> addKeyboardShortcut(String username, String name, String text, int keyCode) {
        return userDao.addKeyboardShortcut(username, name, text, keyCode);
    }

    public List<KeyboardShortcut> getKeyboardShortcutsByUsername(String username) {
        return userDao.getKeyboardShortcutsByUsername(username);
    }

    public ResponseEntity<HttpStatus> deleteKeyboardShortcutByName(String username, String shortcutName) {
        return userDao.deleteKeyboardShortcutByName(username, shortcutName);
    }
}
