package com.zackmurry.nottteme;

import com.zackmurry.nottteme.services.NoteService;
import com.zackmurry.nottteme.services.ShareService;
import com.zackmurry.nottteme.services.UserService;
import com.zackmurry.nottteme.utils.NoteUtils;
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
        assertEquals(HttpStatus.OK, noteService.createNote(noteName, NoteUtils.getBlankNoteBody(), testUsername), "Create note should work.");
        assertEquals(HttpStatus.OK, shareService.shareNoteWithUser(testUsername, noteName, targetUsername), "Sharing a note should work.");
        assertTrue(shareService.noteIsSharedWithUser(noteName, testUsername, targetUsername), "A shared note should be indicated as shared.");
        assertDoesNotThrow(() -> shareService.unshareNoteWithUser(testUsername, noteName, targetUsername), "Unsharing notes should work");
        assertDoesNotThrow(() -> noteService.deleteNote(noteName, testUsername), "Deleting a previously shared note should not throw an error.");
    }



}
