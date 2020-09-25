package com.zackmurry.nottteme;

import com.zackmurry.nottteme.dao.shortcuts.ShortcutDao;
import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.StyleShortcut;
import com.zackmurry.nottteme.models.TextShortcut;
import com.zackmurry.nottteme.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class ShortcutServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ShortcutDao shortcutDao;

    private String testUsername;
    private String testPassword;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeAll
    public void createTestUser() {
        testUsername = RandomStringUtils.randomAlphanumeric(12);

        //create a new test user if this user already exists
        if(userService.accountExists(testUsername)) {
            createTestUser();
        } else {
            testPassword = RandomStringUtils.randomAlphanumeric(12);
            assertTrue(userService.createUserAccount(testUsername, encoder.encode(testPassword), ""));
        }

    }

    @AfterAll
    public void deleteTestUser() {
        assertEquals(HttpStatus.OK, userService.deleteAccount(testUsername));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Tests for text shortcuts")
    class TestTextShortcuts {

        private String name;
        private String key;
        private String text;
        private boolean alt;

        @BeforeAll
        public void initializeTextShortcut() {
            name = RandomStringUtils.randomAlphanumeric(12);
            key = RandomStringUtils.randomAlphanumeric(5);
            text = RandomStringUtils.randomAlphanumeric(10, 60);
            alt = true;
            shortcutDao.addTextShortcut(testUsername, name, text, key, alt);
        }

        @AfterAll
        public void deleteTextShortcut() {
            shortcutDao.deleteTextShortcutByName(testUsername, name);
            assertEquals(0, shortcutDao.getTextShortcutsByUsername(testUsername).size());
        }

        @DisplayName("Test exists")
        @Test
        public void testShortcutCreation() {
            assertTrue(shortcutDao.getTextShortcutsByUsername(testUsername).stream().anyMatch(textShortcut -> textShortcut.getName().equals(name)));
        }

        @DisplayName("Test values are correct")
        @Test
        public void testTextShortcutValues() {
            List<TextShortcut> textShortcuts = shortcutDao.getTextShortcutsByUsername(testUsername);
            assertEquals(1, textShortcuts.size());
            TextShortcut providedTextShortcut = textShortcuts.get(0);
            assertEquals(name, providedTextShortcut.getName());
            assertEquals(key, providedTextShortcut.getKey());
            assertEquals(text, providedTextShortcut.getText());
            assertEquals(alt, providedTextShortcut.getAlt());
        }

        @DisplayName("Test changing values")
        @Test
        public void testChangeShortcutValues() {
            String newName = RandomStringUtils.randomAlphanumeric(12);
            String newKey = RandomStringUtils.randomAlphanumeric(5);
            String newText = RandomStringUtils.randomAlphanumeric(10, 60);
            boolean newAlt = !alt;
            shortcutDao.updateTextShortcutByName(testUsername, name, new TextShortcut(newName, newText, newKey, newAlt));

            //testing that they changed correctly
            List<TextShortcut> textShortcuts = shortcutDao.getTextShortcutsByUsername(testUsername);
            assertEquals(1, textShortcuts.size());
            TextShortcut updatedTextShortcut = textShortcuts.get(0);
            assertEquals(newName, updatedTextShortcut.getName());
            assertEquals(newKey, updatedTextShortcut.getKey());
            assertEquals(newText, updatedTextShortcut.getText());
            assertEquals(newAlt, updatedTextShortcut.getAlt());

            //changing text shortcut back
            shortcutDao.updateTextShortcutByName(testUsername, newName, new TextShortcut(name, text, key, alt));
            textShortcuts = shortcutDao.getTextShortcutsByUsername(testUsername);
            assertEquals(1, textShortcuts.size());
            updatedTextShortcut = textShortcuts.get(0);
            assertEquals(name, updatedTextShortcut.getName());
            assertEquals(key, updatedTextShortcut.getKey());
            assertEquals(text, updatedTextShortcut.getText());
            assertEquals(alt, updatedTextShortcut.getAlt());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Tests for style shortcuts")
    class TestStyleShortcuts {

        private String name;
        private String key;
        private List<CSSAttribute> attributes;
        private boolean alt;

        @BeforeAll
        public void initializeStyleShortcut() {
            name = RandomStringUtils.randomAlphanumeric(12);
            key = RandomStringUtils.randomAlphanumeric(5);
            CSSAttribute attribute = new CSSAttribute(RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(8));
            attributes = new ArrayList<>();
            attributes.add(attribute);
            alt = true;
            shortcutDao.addStyleShortcut(testUsername, name, key, attributes, alt);
        }

        @AfterAll
        public void deleteStyleShortcut() {
            shortcutDao.deleteStyleShortcutByName(testUsername, name);
            assertEquals(0, shortcutDao.getStyleShortcutsByUsername(testUsername).size());
        }

        @DisplayName("Test exists")
        @Test
        public void testShortcutCreation() {
            assertTrue(shortcutDao.getStyleShortcutsByUsername(testUsername).stream().anyMatch(styleShortcut -> styleShortcut.getName().equals(name)));
        }

        @DisplayName("Test values are correct")
        @Test
        public void testShortcutValues() {
            List<StyleShortcut> styleShortcuts = shortcutDao.getStyleShortcutsByUsername(testUsername);
            assertEquals(1, styleShortcuts.size());
            StyleShortcut providedStyleShortcut = styleShortcuts.get(0);
            assertEquals(name, providedStyleShortcut.getName());
            assertEquals(key, providedStyleShortcut.getKey());
            assertEquals(alt, providedStyleShortcut.getAlt());

            List<CSSAttribute> providedCSSAttributes = providedStyleShortcut.getAttributes();
            assertEquals(1, providedCSSAttributes.size());
            CSSAttribute providedAttribute = providedCSSAttributes.get(0);
            assertEquals(attributes.get(0).getAttribute(), providedAttribute.getAttribute());
            assertEquals(attributes.get(0).getValue(), providedAttribute.getValue());
        }

        @DisplayName("Test changing values")
        @Test
        public void testChangeShortcutValues() {
            String newName = RandomStringUtils.randomAlphanumeric(12);
            String newKey = RandomStringUtils.randomAlphanumeric(5);
            CSSAttribute newCSSAttribute = new CSSAttribute(RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(8));
            boolean newAlt = !alt;
            List<CSSAttribute> newAttributes = new ArrayList<>();
            newAttributes.add(newCSSAttribute);
            shortcutDao.updateStyleShortcutByName(testUsername, name, new StyleShortcut(newName, newKey, newAttributes, newAlt));

            //testing that the values changed correctly
            List<StyleShortcut> styleShortcuts = shortcutDao.getStyleShortcutsByUsername(testUsername);
            assertEquals(1, styleShortcuts.size());
            StyleShortcut styleShortcut = styleShortcuts.get(0);
            assertEquals(newName, styleShortcut.getName());
            assertEquals(newKey, styleShortcut.getKey());
            assertEquals(newAlt, styleShortcut.getAlt());
            List<CSSAttribute> providedAttributes = styleShortcut.getAttributes();
            assertEquals(1, providedAttributes.size());
            CSSAttribute providedAttribute = providedAttributes.get(0);
            assertEquals(newCSSAttribute.getAttribute(), providedAttribute.getAttribute());
            assertEquals(newCSSAttribute.getValue(), providedAttribute.getValue());

            //changing shortcut back
            shortcutDao.updateStyleShortcutByName(testUsername, newName, new StyleShortcut(name, key, attributes, alt));
            styleShortcuts = shortcutDao.getStyleShortcutsByUsername(testUsername);
            assertEquals(1, styleShortcuts.size());
            styleShortcut = styleShortcuts.get(0);
            assertEquals(name, styleShortcut.getName());
            assertEquals(key, styleShortcut.getKey());
            assertEquals(alt, styleShortcut.getAlt());
            providedAttributes = styleShortcut.getAttributes();
            assertEquals(1, providedAttributes.size());
            providedAttribute = providedAttributes.get(0);
            CSSAttribute intendedAttribute = attributes.get(0);
            assertEquals(intendedAttribute.getAttribute(), providedAttribute.getAttribute());
            assertEquals(intendedAttribute.getValue(), providedAttribute.getValue());
        }

        //todo test multiple attributes

    }

}
