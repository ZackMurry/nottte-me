package com.zackmurry.nottteme.dao.user;

import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.KeyboardShortcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    boolean createUserAccount(String username, String password);

    boolean accountExists(String username);

    Optional<User> getUserByUsername(String username);

    List<KeyboardShortcut> getKeyboardShortcutsByUsername(String username);

    ResponseEntity<HttpStatus> addKeyboardShortcut(String username, String name, String text, int keyCode);

    ResponseEntity<HttpStatus> deleteKeyboardShortcutByName(String username, String shortcutName);

}
