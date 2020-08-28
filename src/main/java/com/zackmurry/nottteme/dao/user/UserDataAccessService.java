package com.zackmurry.nottteme.dao.user;

import com.google.gson.Gson;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.KeyboardShortcut;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<HttpStatus> addKeyboardShortcut(String username, String name, String text, int keyCode) {
        List<KeyboardShortcut> shortcuts = getKeyboardShortcutsByUsername(username);

        //todo also check for the same keybinding
        if(shortcuts.stream().anyMatch(keyboardShortcut -> keyboardShortcut.getName().equals(name))) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }

        shortcuts.add(new KeyboardShortcut(name, text, keyCode));
        String shortcutString = gson.toJson(shortcuts);

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


    /**
     * todo test and catch failures
     *
     * @param username user to delete shortcut from
     * @param shortcutName shortcut to delete
     * @return an http response of whether it worked out not and where it failed
     */
    @Override
    public ResponseEntity<HttpStatus> deleteKeyboardShortcutByName(String username, String shortcutName) {
        List<KeyboardShortcut> shortcuts = getKeyboardShortcutsByUsername(username);

        shortcuts = shortcuts.stream().filter(keyboardShortcut -> !keyboardShortcut.getName().equals(shortcutName)).collect(Collectors.toList());
        String shortcutString = gson.toJson(shortcuts);

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


}
