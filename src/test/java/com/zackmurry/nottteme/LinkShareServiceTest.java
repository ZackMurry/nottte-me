package com.zackmurry.nottteme;

import com.zackmurry.nottteme.models.sharing.LinkShare;
import com.zackmurry.nottteme.models.sharing.LinkShareRequest;
import com.zackmurry.nottteme.services.LinkShareService;
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
public class LinkShareServiceTest {

    @Autowired
    private LinkShareService linkShareService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private ShareService shareService;

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

    @DisplayName("Test link shares")
    @Test
    public void testLinkShares() {
        assertEquals(HttpStatus.OK, noteService.createNote("test note", testUsername));
        assertEquals(HttpStatus.OK,
                linkShareService.createShareableLink(
                    new LinkShareRequest(
                           "test note",
                           "VIEW",
                           testUsername
                    )
            )
        );
        LinkShare linkShare = linkShareService.getShareableLinksFromNote("test note", testUsername).get(0);
        assertEquals(testUsername, linkShare.getAuthor());
        assertEquals("VIEW", linkShare.getAuthority().getAuthority());
        assertEquals(0, linkShare.getTimesUsed());

        assertDoesNotThrow(() -> linkShareService.useShareableLink(linkShare.getId(), targetUsername));
        assertEquals(1, shareService.getSharesOfNote(testUsername, "test note").size());
        assertEquals(1, linkShareService.getShareableLinksFromNote("test note", testUsername).get(0).getTimesUsed());

        assertEquals(HttpStatus.OK, linkShareService.deleteShareableLink(linkShare.getId(), testUsername));
        assertEquals(HttpStatus.OK, noteService.deleteNote("test note", testUsername));
    }


}
