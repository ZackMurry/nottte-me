package com.zackmurry.nottteme.dao.user;

import com.google.gson.Gson;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;
import javassist.NotFoundException;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * used for accessing and updating data about users and their preferences
 * todo maybe move shortcuts into their own table and thus their own class for services et al
 * todo only allow one attribute of each attribute type per shortcut (literally doesn't make sense to override itself and makes it easier to index)
 */
@Service
public final class UserDataAccessService implements UserDao {


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
                            resultSet.getString(2) //password
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
    public List<TextShortcut> getTextShortcutsByUsername(String username) {
        String sql = "SELECT text_shortcuts FROM users WHERE username=?";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            return User.convertTextShortcutStringToObjects(shortcutString);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<TextShortcut> getTextShortcutsByUsernameOrderedByName(String username) {
        String sql = "SELECT text_shortcuts FROM users WHERE username=?";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            List<TextShortcut> shortcuts = User.convertTextShortcutStringToObjects(shortcutString);
            shortcuts.sort(Comparator.comparing(TextShortcut::getName)); //taking list and sorting it by the name attribute of KeyboardShortcut
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
    public HttpStatus addTextShortcut(String username, String name, String text, String key) {
        List<TextShortcut> textShortcuts = getTextShortcutsByUsername(username);

        //checking if any existing text shortcuts have the same name
        if(textShortcuts.stream().anyMatch(textShortcut -> textShortcut.getName().equals(name))) {
            return HttpStatus.PRECONDITION_FAILED;
        }

        //checking if any style shortcuts have the same name
        List<StyleShortcut> styleShortcuts = getStyleShortcutsByUsername(username);
        if(styleShortcuts.stream().anyMatch(styleShortcut -> styleShortcut.getName().equals(name))) {
            return HttpStatus.PRECONDITION_FAILED;
        }

        textShortcuts.add(new TextShortcut(name, text, key));
        return setTextShortcutsByName(username, textShortcuts);
    }


    /**
     * deletes a keyboard shortcut by name
     *
     * @param username user to delete shortcut from
     * @param shortcutName shortcut to delete
     * @return an http response of whether it worked out not and where it failed
     */
    @Override
    public HttpStatus deleteTextShortcutByName(String username, String shortcutName) {
        List<TextShortcut> shortcuts = getTextShortcutsByUsername(username);
        if(shortcuts.isEmpty()) return HttpStatus.BAD_REQUEST;
        shortcuts = shortcuts.stream().filter(keyboardShortcut -> !keyboardShortcut.getName().equals(shortcutName)).collect(Collectors.toList());
        return setTextShortcutsByName(username, shortcuts);
    }

    @Override
    public HttpStatus updateTextShortcutByName(String username, String shortcutName, TextShortcut updatedTextShortcut) {
        List<TextShortcut> shortcuts = getTextShortcutsByUsername(username);
        if(shortcuts.isEmpty()) return HttpStatus.BAD_REQUEST;

        //finds shortcuts with matching name and sets it/them to the newKeyboardShortcut
        shortcuts = shortcuts.stream()
                .map(textShortcut -> {
                    if(textShortcut.getName().equals(shortcutName)) return updatedTextShortcut;
                    return textShortcut;
        }).collect(Collectors.toList());
        return setTextShortcutsByName(username, shortcuts);
    }

    @Override
    public HttpStatus setTextShortcutsByName(String username, List<TextShortcut> updatedTextShortcuts) {
        String shortcutString = gson.toJson(updatedTextShortcuts);
        String sql = "UPDATE users SET text_shortcuts = ? WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    shortcutString,
                    username
            );
            return HttpStatus.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
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

    //todo not allow two shortcuts with the same key (only one would get activated because of returning)
    @Override
    public HttpStatus addStyleShortcut(String username, String name, String key, List<CSSAttribute> attributes) {
        List<StyleShortcut> styleShortcuts = getStyleShortcutsByUsername(username);

        //checking if any existing style shortcuts have the same name as the new shortcut's name
        if(styleShortcuts.stream().anyMatch(styleShortcut -> styleShortcut.getName().equals(name))) {
            return HttpStatus.PRECONDITION_FAILED;
        }


        //checking for text shortcuts with the same name
        List<TextShortcut> textShortcuts = getTextShortcutsByUsername(username);
        if(!textShortcuts.isEmpty() && textShortcuts.stream().anyMatch(textShortcut -> textShortcut.getName().equals(name))) {
            return HttpStatus.PRECONDITION_FAILED;
        }

        styleShortcuts.add(new StyleShortcut(name, key, attributes));
        return setStyleShortcutsByName(username, styleShortcuts);
    }

    @Override
    public HttpStatus setStyleShortcutsByName(String username, List<StyleShortcut> updatedStyleShortcuts) {
        String shortcutString = gson.toJson(updatedStyleShortcuts);
        String sql = "UPDATE users SET style_shortcuts = ? WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    shortcutString,
                    username
            );
            return HttpStatus.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }
    }

    @Override
    public HttpStatus deleteStyleShortcutByName(String username, String shortcutName) {
        List<StyleShortcut> shortcuts = getStyleShortcutsByUsername(username);
        if(shortcuts.isEmpty()) return HttpStatus.BAD_REQUEST;
        shortcuts = shortcuts.stream().filter(styleShortcut -> !styleShortcut.getName().equals(shortcutName)).collect(Collectors.toList());
        return setStyleShortcutsByName(username, shortcuts);
    }

    @Override
    public HttpStatus updateStyleShortcutByName(String username, String shortcutName, StyleShortcut updatedStyleShortcut) {
        List<StyleShortcut> shortcuts = getStyleShortcutsByUsername(username);
        if(shortcuts.isEmpty()) return HttpStatus.BAD_REQUEST;

        //finds shortcuts with matching name and sets it/them to the newStyleShortcut
        shortcuts = shortcuts.stream()
                .map(styleShortcut -> {
                    if(styleShortcut.getName().equals(shortcutName)) return updatedStyleShortcut;
                    return styleShortcut;
                }).collect(Collectors.toList());
        return setStyleShortcutsByName(username, shortcuts);
    }

    @Override
    public List<StyleShortcut> getStyleShortcutsByUsernameOrderedByName(String username) {
        String sql = "SELECT style_shortcuts FROM users WHERE username=?";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            List<StyleShortcut> shortcuts = User.convertStyleShortcutStringToObjects(shortcutString);
            shortcuts.sort(Comparator.comparing(StyleShortcut::getName)); //taking list and sorting it by the name attribute of KeyboardShortcut
            return shortcuts;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public HttpStatus deleteAccount(String username) {
        if(!accountExists(username)) {
            return HttpStatus.NOT_FOUND;
        }

        String sql = "DELETE FROM users WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

    }

    @Override
    public HttpStatus addCSSAttributeToStyleShortcut(String username, String shortcutName, CSSAttribute attribute) {
        if(!accountExists(username)) return HttpStatus.NOT_FOUND;

        List<StyleShortcut> styleShortcuts = getStyleShortcutsByUsername(username);

        List<StyleShortcut> styleShortcutsWithMatchingName = styleShortcuts.stream().filter(styleShortcut -> styleShortcut.getName().equals(shortcutName)).collect(Collectors.toList());
        if(styleShortcutsWithMatchingName.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        if(styleShortcutsWithMatchingName.size() > 1) {
            return HttpStatus.PRECONDITION_FAILED;
        }

        StyleShortcut shortcut = styleShortcutsWithMatchingName.get(0);
        List<CSSAttribute> attributes = shortcut.getAttributes();
        attributes.add(attribute);
        shortcut.setAttributes(attributes);
        setStyleShortcutsByName(username, styleShortcuts);
        return HttpStatus.OK;
    }

    @Override
    public HttpStatus removeCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName) {
        if(!accountExists(username)) return HttpStatus.NOT_FOUND;

        List<StyleShortcut> styleShortcuts = getStyleShortcutsByUsername(username);
        List<StyleShortcut> styleShortcutsWithMatchingName = styleShortcuts.stream().filter(styleShortcut -> styleShortcut.getName().equals(shortcutName)).collect(Collectors.toList());

        if(styleShortcutsWithMatchingName.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        if(styleShortcutsWithMatchingName.size() > 1) {
            return HttpStatus.PRECONDITION_FAILED;
        }

        StyleShortcut shortcut = styleShortcutsWithMatchingName.get(0);
        shortcut.setAttributes(shortcut.getAttributes().stream().filter(attribute -> !attribute.getAttribute().equals(attributeName)).collect(Collectors.toList()));
        return setStyleShortcutsByName(username, styleShortcuts);
    }

    @Override
    public CSSAttribute getCSSAttributeFromStyleShortcut(String username, String shortcutName, String attributeName) throws NotFoundException {
        if(!accountExists(username)) throw new NotFoundException("Cannot find user with name " + username + ".");
        List<StyleShortcut> styleShortcuts = getStyleShortcutsByUsername(username);
        Optional<StyleShortcut> optionalShortcut = styleShortcuts.stream().filter(styleShortcut -> styleShortcut.getName().equals(shortcutName)).findFirst();

        if(optionalShortcut.isEmpty()) throw new NotFoundException("Cannot find shortcut with name " + shortcutName + " of user " + username + ".");
        StyleShortcut shortcut = optionalShortcut.get();
        Optional<CSSAttribute> optionalCSSAttribute = shortcut.getAttributes().stream().filter(attribute -> attribute.getAttribute().equals(attributeName)).findFirst();
        if(optionalCSSAttribute.isEmpty()) throw new NotFoundException("Cannot find CSS attribute with attribute " + attributeName + " in shortcut " + shortcutName + " of user " + username + ".");
        return optionalCSSAttribute.get();
    }

    @Override
    public StyleShortcut getStyleShortcutByUsername(String username, String shortcutName) throws NotFoundException {
        if(!accountExists(username)) throw new NotFoundException("Cannot find user with name " + username + ".");
        List<StyleShortcut> styleShortcuts = getStyleShortcutsByUsername(username);
        Optional<StyleShortcut> optionalShortcut = styleShortcuts.stream().filter(styleShortcut -> styleShortcut.getName().equals(shortcutName)).findFirst();
        if(optionalShortcut.isEmpty()) throw new NotFoundException("Cannot find style shortcut with name " + shortcutName + " of user " + username + ".");
        return optionalShortcut.get();
    }

    @Override
    public List<CSSAttribute> getCSSAttributesFromStyleShortcut(String username, String shortcutName) throws NotFoundException {
        if(!accountExists(username)) throw new NotFoundException("Cannot find user with name " + username + ".");
        List<StyleShortcut> styleShortcuts = getStyleShortcutsByUsername(username);
        Optional<StyleShortcut> optionalShortcut = styleShortcuts.stream().filter(styleShortcut -> styleShortcut.getName().equals(shortcutName)).findFirst();
        if(optionalShortcut.isEmpty()) throw new NotFoundException("Cannot find style shortcut with name " + shortcutName + " of user " + username + ".");
        return optionalShortcut.get().getAttributes();
    }

}
