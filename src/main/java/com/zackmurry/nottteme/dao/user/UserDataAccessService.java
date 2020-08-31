package com.zackmurry.nottteme.dao.user;

import com.google.gson.Gson;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.KeyboardShortcut;
import com.zackmurry.nottteme.models.StyleShortcut;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDataAccessService implements UserDao {


    private final Gson gson = new Gson();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }


    @Override
    public boolean createUserAccount(String username, String password) {
        if(accountExists(username)) return false;

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try {
            jdbcTemplate.execute(
                    sql,
                    username,
                    password
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean accountExists(String username) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE username=?)";

        try {
            return jdbcTemplate.queryForBoolean(
                    sql,
                    username
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=? LIMIT 1";

        try {
            List<User> list = jdbcTemplate.query(
                    sql,
                    resultSet -> new User(
                            resultSet.getString(1), //username
                            resultSet.getString(2), //password
                            resultSet.getString(3) //shortcuts
                            ),
                    username
            );
            return Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<KeyboardShortcut> getKeyboardShortcutsByUsername(String username) {
        String sql = "SELECT shortcuts FROM users WHERE username=?";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            return User.convertKeyboardShortcutStringToObjects(shortcutString);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<KeyboardShortcut> getKeyboardShortcutsByUsernameOrderedByName(String username) {
        String sql = "SELECT shortcuts FROM users WHERE username=?";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            List<KeyboardShortcut> shortcuts = User.convertKeyboardShortcutStringToObjects(shortcutString);
            shortcuts.sort(Comparator.comparing(KeyboardShortcut::getName)); //taking list and sorting it by the name attribute of KeyboardShortcut
            return shortcuts;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * adds a keyboard shortcut with the attributes described in the params
     *
     * @param username name of user to add shortcut to
     * @param name name of shortcut
     * @param text text to insert when shortcut is called
     * @param key key to type with control (todo multiple keys at the same time)
     * @return http response describing success/fail
     */
    @Override
    public ResponseEntity<HttpStatus> addKeyboardShortcut(String username, String name, String text, String key) {
        List<KeyboardShortcut> shortcuts = getKeyboardShortcutsByUsername(username);

        //todo also check for the same keybinding
        if(shortcuts.stream().anyMatch(keyboardShortcut -> keyboardShortcut.getName().equals(name))) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        shortcuts.add(new KeyboardShortcut(name, text, key));
        return setKeyboardShortcutsByName(username, shortcuts);
    }


    /**
     * deletes a keyboard shortcut by name
     *
     * @param username user to delete shortcut from
     * @param shortcutName shortcut to delete
     * @return an http response of whether it worked out not and where it failed
     */
    @Override
    public ResponseEntity<HttpStatus> deleteKeyboardShortcutByName(String username, String shortcutName) {
        List<KeyboardShortcut> shortcuts = getKeyboardShortcutsByUsername(username);
        if(shortcuts.isEmpty()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        shortcuts = shortcuts.stream().filter(keyboardShortcut -> !keyboardShortcut.getName().equals(shortcutName)).collect(Collectors.toList());
        return setKeyboardShortcutsByName(username, shortcuts);
    }

    @Override
    public ResponseEntity<HttpStatus> updateKeyboardShortcutByName(String username, String shortcutName, KeyboardShortcut updatedKeyboardShortcut) {
        List<KeyboardShortcut> shortcuts = getKeyboardShortcutsByUsername(username);
        if(shortcuts.isEmpty()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //finds shortcuts with matching name and sets it/them to the newKeyboardShortcut
        shortcuts = shortcuts.stream()
                .map(keyboardShortcut -> {
                    if(keyboardShortcut.getName().equals(shortcutName)) return updatedKeyboardShortcut;
                    return keyboardShortcut;
        }).collect(Collectors.toList());
        return setKeyboardShortcutsByName(username, shortcuts);
    }

    @Override
    public ResponseEntity<HttpStatus> setKeyboardShortcutsByName(String username, List<KeyboardShortcut> updatedKeyboardShortcuts) {
        String shortcutString = gson.toJson(updatedKeyboardShortcuts);
        String sql = "UPDATE users SET shortcuts = ? WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    shortcutString,
                    username
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<StyleShortcut> getStyleShortcutsByUsername(String username) {
        String sql = "SELECT style_shortcuts FROM users WHERE username=?";

        try {
            String styleShortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            return User.convertStyleShortcutStringToObjects(styleShortcutString);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    @Override
    public ResponseEntity<HttpStatus> addStyleShortcut(String username, String name, String key, String attribute, String value) {
        List<StyleShortcut> shortcuts = getStyleShortcutsByUsername(username);

        //todo also check for the same keybinding
        if(shortcuts.stream().anyMatch(styleShortcut -> styleShortcut.getName().equals(name))) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        shortcuts.add(new StyleShortcut(name, key, attribute, value));
        return setStyleShortcutsByName(username, shortcuts);
    }

    @Override
    public ResponseEntity<HttpStatus> setStyleShortcutsByName(String username, List<StyleShortcut> updatedStyleShortcuts) {
        String shortcutString = gson.toJson(updatedStyleShortcuts);
        String sql = "UPDATE users SET style_shortcuts = ? WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    shortcutString,
                    username
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> deleteStyleShortcutByName(String username, String shortcutName) {
        List<StyleShortcut> shortcuts = getStyleShortcutsByUsername(username);
        if(shortcuts.isEmpty()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        shortcuts = shortcuts.stream().filter(styleShortcut -> !styleShortcut.getName().equals(shortcutName)).collect(Collectors.toList());
        return setStyleShortcutsByName(username, shortcuts);
    }

    @Override
    public ResponseEntity<HttpStatus> updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut) {
        List<StyleShortcut> shortcuts = getStyleShortcutsByUsername(username);
        if(shortcuts.isEmpty()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        //finds shortcuts with matching name and sets it/them to the newStyleShortcut
        shortcuts = shortcuts.stream()
                .map(styleShortcut -> {
                    if(styleShortcut.getName().equals(shortcutName)) return updatedStyleShortcut;
                    return styleShortcut;
                }).collect(Collectors.toList());
        return setStyleShortcutsByName(username, shortcuts);
    }


}
