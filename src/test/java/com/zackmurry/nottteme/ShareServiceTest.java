package com.zackmurry.nottteme;

import com.zackmurry.nottteme.services.NoteService;
import com.zackmurry.nottteme.services.ShareService;
import com.zackmurry.nottteme.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    //todo add test for duplicating shared notes

}
