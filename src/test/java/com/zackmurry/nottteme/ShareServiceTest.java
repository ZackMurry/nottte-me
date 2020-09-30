package com.zackmurry.nottteme;

import com.zackmurry.nottteme.dao.notes.NoteDataAccessService;
import com.zackmurry.nottteme.models.CSSAttribute;
import com.zackmurry.nottteme.models.shortcuts.StyleShortcut;
import com.zackmurry.nottteme.services.NoteService;
import com.zackmurry.nottteme.services.ShareService;
import com.zackmurry.nottteme.services.ShortcutService;
import com.zackmurry.nottteme.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class ShareServiceTest {

    @Autowired
    private ShareService shareService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private ShortcutService shortcutService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private String testUsername;
    private String testPassword;

    private String targetUsername;
    private String targetPassword;

    @BeforeAll
    public void createTestUser() {
        testUsername = RandomStringUtils.randomAlphanumeric(12);
        targetUsername = RandomStringUtils.randomAlphanumeric(12);

        //create a new test user if this user already exists
        if(userService.accountExists(testUsername) || userService.accountExists(targetUsername)) {
            createTestUser();
        } else {
            testPassword = RandomStringUtils.randomAlphanumeric(12);
            assertTrue(userService.createUserAccount(testUsername, encoder.encode(testPassword), ""));

            targetPassword = RandomStringUtils.randomAlphanumeric(12);
            assertTrue(userService.createUserAccount(targetUsername, encoder.encode(targetPassword), ""));
        }

    }

    @AfterAll
    public void deleteTestUser() {
        assertEquals(HttpStatus.OK, userService.deleteAccount(testUsername));
        assertEquals(HttpStatus.OK, userService.deleteAccount(targetUsername));
        assertEquals(HttpStatus.OK, noteService.deleteNotesByAuthor(testUsername));
    }

    @DisplayName("Test sharing note")
    @Test
    public void testSharing() {
        String noteName = RandomStringUtils.randomAlphabetic(10, 20);
        assertEquals(HttpStatus.OK, noteService.createNote(noteName, testUsername), "Create note should work.");
        assertEquals(HttpStatus.OK, shareService.shareNoteWithUser(testUsername, noteName, targetUsername), "Sharing a note should work.");
        assertTrue(shareService.noteIsSharedWithUser(noteName, testUsername, targetUsername), "A shared note should be indicated as shared.");
        assertDoesNotThrow(() -> shareService.unshareNoteWithUser(testUsername, noteName, targetUsername), "Unsharing notes should work");
        assertDoesNotThrow(() -> noteService.deleteNote(noteName, testUsername), "Deleting a previously shared note should not throw an error.");
    }

    @DisplayName("Test shares of note")
    @Test
    public void testGetSharesOfNote()  {
        final String noteName = RandomStringUtils.randomAlphabetic(10, 20);
        assertEquals(HttpStatus.OK, noteService.createNote(noteName, testUsername), "Creating a note should work.");
        assertEquals(0, shareService.getSharesOfNote(testUsername, noteName).size());
        assertEquals(HttpStatus.OK, shareService.shareNoteWithUser(testUsername, noteName, targetUsername), "Sharing a note should work.");
        assertEquals(targetUsername, shareService.getSharesOfNote(testUsername, noteName).get(0), "Note should be shared with target user.");
        assertEquals(HttpStatus.OK, shareService.unshareNoteWithUser(testUsername, noteName, targetUsername), "Unsharing a note should work.");
        assertEquals(0, shareService.getSharesOfNote(testUsername, noteName).size(), "Note should not be shared with anyone.");
        assertEquals(HttpStatus.OK, noteService.deleteNote(noteName, testUsername), "Deleting a note should work.");
    }

    @DisplayName("Test sharing on note rename")
    @Test
    public void testSharingRename() {
        final String noteName = RandomStringUtils.randomAlphabetic(10, 20);
        assertEquals(HttpStatus.OK, noteService.createNote(noteName, testUsername), "Creating a standard note should work.");
        assertEquals(HttpStatus.OK, shareService.shareNoteWithUser(testUsername, noteName, targetUsername), "Sharing a note should work.");
        assertTrue(shareService.noteIsSharedWithUser(noteName, testUsername, targetUsername), "Note should be shared with target user.");

        final String newNoteName = RandomStringUtils.randomAlphanumeric(10, 20);
        assertEquals(HttpStatus.OK, noteService.renameNote(noteName, newNoteName, testUsername), "Renaming a note should work.");
        assertFalse(shareService.noteIsSharedWithUser(noteName, testUsername, targetUsername), "Once a note is renamed, its old name should no longer be shared with anyone.");
        assertTrue(shareService.noteIsSharedWithUser(newNoteName, testUsername, targetUsername), "Once a note is renamed, getting shares by new name should work.");

        //clean up
        assertEquals(HttpStatus.OK, noteService.deleteNote(newNoteName, testUsername), "Deleting a note should work.");
        assertFalse(shareService.noteIsSharedWithUser(newNoteName, testUsername, targetUsername), "Note should not be shared with user once it is deleted.");
    }

    @DisplayName("Test duplicating shared notes")
    @Test
    public void testDuplicateShared() {
        final String noteName = RandomStringUtils.randomAlphabetic(10, 20);
        assertEquals(HttpStatus.OK, noteService.createNote(noteName, testUsername));

        //sharing note
        assertEquals(HttpStatus.OK, shareService.shareNoteWithUser(testUsername, noteName, targetUsername));

        //creating shortcuts on target user's side
        List<CSSAttribute> targetAttributesList1 = new ArrayList<>();
        targetAttributesList1.add(new CSSAttribute("color", "blue"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(targetUsername, "myFirstStyleShortcut", "O", targetAttributesList1, true));

        List<CSSAttribute> targetAttributesList2 = new ArrayList<>();
        targetAttributesList2.add(new CSSAttribute("background-color", "purple"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(targetUsername, testUsername + "-color-blue", "L", targetAttributesList2, false));

        List<CSSAttribute> targetAttributeList3 = new ArrayList<>();
        targetAttributeList3.add(new CSSAttribute("font-size", "24px"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(targetUsername, "testMergeShortcuts", "q", targetAttributeList3, false));

        List<CSSAttribute> targetAttributeList4 = new ArrayList<>();
        targetAttributeList4.add(new CSSAttribute("border", "4px black solid"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(targetUsername, "testNoMergeShortcuts", "G", targetAttributeList4, true));

        //creating style shortcuts to transfer over
        List<CSSAttribute> attribute1List = new ArrayList<>();
        attribute1List.add(new CSSAttribute("color", "blue"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(testUsername, "myCoolStyleShortcut", "K", attribute1List, true));

        List<CSSAttribute> attribute2List = new ArrayList<>();
        attribute2List.add(new CSSAttribute("text-decoration", "underline"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(testUsername, "mySecondStyleShortcut", "U", attribute2List, true));

        List<CSSAttribute> attribute3List = new ArrayList<>();
        attribute3List.add(new CSSAttribute("font-size", "24px"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(testUsername, "myThirdStyleShortcut", "K", attribute3List, false));

        List<CSSAttribute> attribute4List = new ArrayList<>();
        attribute4List.add(new CSSAttribute("color", "blue"));
        attribute4List.add(new CSSAttribute("border", "4px black solid"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(testUsername, "myFourthStyleShortcut", "l", attribute4List, true));

        List<CSSAttribute> attribute5List = new ArrayList<>();
        attribute5List.add(new CSSAttribute("color", "blue"));
        attribute5List.add(new CSSAttribute("text-decoration", "underline"));
        assertEquals(HttpStatus.OK, shortcutService.addStyleShortcut(testUsername, "testIndexing", "E", attribute5List, false));

        //duplicating note
        assertEquals(HttpStatus.OK, shareService.duplicateSharedNote(testUsername, noteName, targetUsername));

        //checking values
        List<StyleShortcut> sharedShortcuts = shortcutService.getSharedStyleShortcutsByUser(targetUsername);
        assertEquals(3, sharedShortcuts.size(), "Matching shortcuts should eliminate two unnecessary shortcuts.");

        assertEquals(testUsername + "-text-decoration-underline", sharedShortcuts.get(0).getName());
        assertEquals("U", sharedShortcuts.get(0).getKey());
        assertEquals(attribute2List, sharedShortcuts.get(0).getAttributes());
        assertTrue(sharedShortcuts.get(0).getAlt());

        assertEquals(testUsername + "-color-blue-1", sharedShortcuts.get(1).getName(), "A pre-existing shortcut with the target name should increase the index of the new name.");
        assertEquals("l", sharedShortcuts.get(1).getKey());
        assertEquals(attribute4List, sharedShortcuts.get(1).getAttributes());
        assertTrue(sharedShortcuts.get(1).getAlt());

        assertEquals(testUsername + "-color-blue-2", sharedShortcuts.get(2).getName(), "A newly added shortcut with the target name should increase the index of the new name.");
        assertEquals("E", sharedShortcuts.get(2).getKey());
        assertEquals(attribute5List, sharedShortcuts.get(2).getAttributes());
        assertFalse(sharedShortcuts.get(2).getAlt());

        //cleaning up
        assertEquals(HttpStatus.OK, shortcutService.deleteStyleShortcutsByUser(testUsername));
        assertEquals(HttpStatus.OK, shortcutService.deleteStyleShortcutsByUser(targetUsername));
        assertEquals(HttpStatus.OK, shortcutService.deleteSharedStyleShortcutsByUser(targetUsername));
        assertEquals(HttpStatus.OK, noteService.deleteNote(noteName, testUsername));
        assertEquals(HttpStatus.OK, noteService.deleteNote(NoteDataAccessService.COPY_NOTE_PREFIX + noteName + NoteDataAccessService.COPY_NOTE_SUFFIX, targetUsername));
    }

}
