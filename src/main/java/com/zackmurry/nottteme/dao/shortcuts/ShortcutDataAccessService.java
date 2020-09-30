package com.zackmurry.nottteme.dao.shortcuts;

import com.google.gson.Gson;
import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.shortcuts.GeneratedShortcut;
import com.zackmurry.nottteme.models.shortcuts.StyleShortcut;
import com.zackmurry.nottteme.models.shortcuts.TextShortcut;
import com.zackmurry.nottteme.utils.ShortcutUtils;
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

@Service
public final class ShortcutDataAccessService implements ShortcutDao {

    private final Gson gson = new Gson();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ShortcutDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public List<TextShortcut> getTextShortcutsByUsername(String username) {
        String sql = "SELECT text_shortcuts FROM shortcuts WHERE username=?";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            return ShortcutUtils.convertTextShortcutStringToObjects(shortcutString);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<TextShortcut> getTextShortcutsByUsernameOrderedByName(String username) {
        String sql = "SELECT text_shortcuts FROM shortcuts WHERE username=?";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            List<TextShortcut> shortcuts = ShortcutUtils.convertTextShortcutStringToObjects(shortcutString);
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
    public HttpStatus addTextShortcut(String username, String name, String text, String key, boolean alt) {
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

        textShortcuts.add(new TextShortcut(name, text, key, alt));
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
        String sql = "UPDATE shortcuts SET text_shortcuts = ? WHERE username=?";

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
        String sql = "SELECT style_shortcuts FROM shortcuts WHERE username=?";

        try {
            String styleShortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            return ShortcutUtils.convertStyleShortcutStringToObjects(styleShortcutString);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    //todo not allow two shortcuts with the same key (only one would get activated because of returning)
    @Override
    public HttpStatus addStyleShortcut(String username, String name, String key, List<CSSAttribute> attributes, boolean alt) {
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

        styleShortcuts.add(new StyleShortcut(name, key, attributes, alt));
        return setStyleShortcutsByName(username, styleShortcuts);
    }

    @Override
    public HttpStatus setStyleShortcutsByName(String username, List<StyleShortcut> updatedStyleShortcuts) {
        String shortcutString = gson.toJson(updatedStyleShortcuts);
        String sql = "UPDATE shortcuts SET style_shortcuts = ? WHERE username=?";

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
        String sql = "SELECT style_shortcuts FROM shortcuts WHERE username=?";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            List<StyleShortcut> shortcuts = ShortcutUtils.convertStyleShortcutStringToObjects(shortcutString);
            shortcuts.sort(Comparator.comparing(StyleShortcut::getName)); //taking list and sorting it by the name attribute of KeyboardShortcut
            return shortcuts;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean accountExists(String username) {
        String sql = "SELECT EXISTS (SELECT 1 FROM shortcuts WHERE username=?)";

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
        if(shortcut.getAttributes().stream().noneMatch(attribute -> attribute.getAttribute().equals(attributeName))) {
            return HttpStatus.NOT_FOUND;
        }
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

    @Override
    public List<StyleShortcut> getSharedStyleShortcutsByUser(String username) {

        //todo probably edit queries to include limits where only one row will match
        //todo make new obj for shared shortcuts to ignore keys and alt
        String sql = "SELECT shared_style_shortcuts FROM shortcuts WHERE username=? LIMIT 1";

        try {
            String rawSharedStyleShortcuts = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            return ShortcutUtils.convertStyleShortcutStringToObjects(rawSharedStyleShortcuts);
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private HttpStatus setSharedStyleShortcutsByUser(String username, List<StyleShortcut> sharedStyleShortcuts) {
        String shortcutString = gson.toJson(sharedStyleShortcuts);
        String sql = "UPDATE shortcuts SET shared_style_shortcuts = ? WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    shortcutString,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public HttpStatus addSharedStyleShortcutsToUser(String username, List<StyleShortcut> newStyleShortcuts) {
        if(!accountExists(username)) return HttpStatus.NOT_FOUND;
        List<StyleShortcut> sharedStyleShortcuts = getSharedStyleShortcutsByUser(username);
        sharedStyleShortcuts.addAll(newStyleShortcuts);
        return setSharedStyleShortcutsByUser(username, sharedStyleShortcuts);
    }

    @Override
    public HttpStatus deleteStyleShortcutsByUser(String username) {
        if(!accountExists(username)) return HttpStatus.NOT_MODIFIED;
        String sql = "UPDATE shortcuts SET style_shortcuts = '[]' WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public HttpStatus deleteSharedStyleShortcutsByUser(String username) {
        if(!accountExists(username)) return HttpStatus.NOT_MODIFIED;
        String sql = "UPDATE shortcuts SET shared_style_shortcuts ='[]' WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    @Override
    public HttpStatus deleteTextShortcutsByUser(String username) {
        if(!accountExists(username)) return HttpStatus.NOT_MODIFIED;
        String sql = "UPDATE shortcuts SET text_shortcuts = '[]' WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public HttpStatus deleteGeneratedShortcutsByUser(String username) {
        if(!accountExists(username)) return HttpStatus.NOT_MODIFIED;
        String sql = "UPDATE shortcuts SET generated_shortcuts = '[]' WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public HttpStatus addGeneratedShortcut(String username, GeneratedShortcut generatedShortcut) {
        if(!accountExists(username)) return HttpStatus.NOT_FOUND;
        List<GeneratedShortcut> shortcuts = getGeneratedShortcutsByUser(username);
        shortcuts.add(generatedShortcut);
        return setGeneratedShortcutsByUser(username, shortcuts);
    }

    @Override
    public List<GeneratedShortcut> getGeneratedShortcutsByUser(String username) {
        String sql = "SELECT generated_shortcuts FROM shortcuts WHERE username=? LIMIT 1";

        try {
            String shortcutString = jdbcTemplate.queryForString(
                    sql,
                    username
            );
            return ShortcutUtils.convertGeneratedShortcutStringToObjects(shortcutString);
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    @Override
    public HttpStatus setGeneratedShortcutsByUser(String username, List<GeneratedShortcut> generatedShortcuts) {
        if(!accountExists(username)) return HttpStatus.NOT_FOUND;
        String shortcutString = gson.toJson(generatedShortcuts);
        String sql = "UPDATE shortcuts SET generated_shortcuts = ? WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    shortcutString,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

}
