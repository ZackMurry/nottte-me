package com.zackmurry.nottteme;

import com.zackmurry.nottteme.dao.notes.NoteDataAccessService;
import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.services.NoteService;
import com.zackmurry.nottteme.services.UserService;
import com.zackmurry.nottteme.utils.NoteUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private String testUsername;
    private String testPassword;

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


    /**
     * used for tests that use standard note titles. the only tests that won't use this will probably be edge cases that occur when creating notes
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Tests for notes which create a note and destroy it at the end")
    class TestsWithInitializedNotes {

        private String noteTitle;

        /**
         * creates a note with an empty body, whose name is assigned to noteTitle
         */
        @BeforeEach
        public void initializeNote() {
            noteTitle = RandomStringUtils.randomAlphabetic(10, 20);
            assertEquals(HttpStatus.OK, noteService.createNote(noteTitle, testUsername), "Creating a note with a random name should work.");
            assertEquals(1, noteService.getNoteCount(testUsername));
        }

        /**
         * deletes the created note
         */
        @AfterEach
        public void deleteNote() {
            assertEquals(HttpStatus.OK, noteService.deleteNote(noteTitle, testUsername));
            assertEquals(0, noteService.getNoteCount(testUsername));
        }

        @DisplayName("Test creating and deleting notes")
        @Test
        public void testCreateNote() {
            assertTrue(noteService.userHasNote(noteTitle, testUsername));
            assertEquals(HttpStatus.PRECONDITION_FAILED, noteService.createNote(noteTitle, testUsername), "Creating a duplicate note should not work.");
        }

        @DisplayName("Test duplicating notes")
        @Test
        public void testDuplicating() {
            assertEquals(HttpStatus.OK, noteService.duplicateNote(noteTitle, testUsername));
            assertEquals(2, noteService.getNotesByUser(testUsername).size(), "After duplicating their only note, a user should have two notes.");
            final String duplicatedNoteTitle = NoteDataAccessService.COPY_NOTE_PREFIX + noteTitle + NoteDataAccessService.COPY_NOTE_SUFFIX;
            Optional<Note> optionalDuplicatedNote = noteService.getNote(duplicatedNoteTitle, testUsername);
            Optional<Note> optionalOriginalNote = noteService.getNote(noteTitle, testUsername);
            assertTrue(optionalDuplicatedNote.isPresent(), "A duplicated note should be present when it is fetched.");
            assertTrue(optionalOriginalNote.isPresent(), "After a note is duplicated, it should still exist.");
            Note duplicatedNote = optionalDuplicatedNote.get();
            Note originalNote = optionalOriginalNote.get();
            assertNotEquals(originalNote, duplicatedNote, "The two notes should not be identical (titles changes).");
            assertEquals(originalNote.getBody(), duplicatedNote.getBody(), "The two notes should have the same bodies.");
            assertEquals(originalNote.getAuthor(), duplicatedNote.getAuthor(), "The two notes should have the same authors.");
            assertEquals(HttpStatus.OK, noteService.deleteNote(duplicatedNoteTitle, testUsername), "Deleting the duplicated note should not throw an error.");
        }

        @DisplayName("Test getting a raw note")
        @Test
        public void testGetRawNote() {
            assertEquals(1, noteService.getNoteCount(testUsername), "After creating a user's first note, they should have one note.");
            assertEquals(NoteUtils.getBlankNoteBody(), noteService.getRawNote(noteTitle, testUsername), "After creating a note, the note should have the body that it was assigned.");

            //non-blank body
            final String secondNoteTitle = RandomStringUtils.randomAlphanumeric(12);
            final String customBody = "{\"blocks\":[{\"key\":\"nottte\",\"text\":\"this is a body that will be used for JUnit testing. if you're reading this, hi! i hope that this test works.\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[{\"offset\":12,\"length\":13,\"style\":\"personal\"}],\"entityRanges\":[],\"data\":{}}],\"entityMap\":{}}";
            assertEquals(HttpStatus.OK, noteService.createNote(secondNoteTitle, customBody, testUsername), "Creating a note with a custom body should work.");
            assertEquals(2, noteService.getNoteCount(testUsername), "After a user creates two notes, they should have two notes.");
            assertEquals(customBody, noteService.getRawNote(secondNoteTitle, testUsername));
            assertEquals(HttpStatus.OK, noteService.deleteNote(secondNoteTitle, testUsername));
        }

        @DisplayName("Test save note")
        @Test
        public void testSaveNote() {
            final String newBody = "{\"blocks\":[{\"key\":\"nottte\",\"text\":\"this is a body that will be used for JUnit testing. if you're reading this, hi! i hope that this test works.\",\"type\":\"unstyled\",\"depth\":0,\"inlineStyleRanges\":[{\"offset\":12,\"length\":13,\"style\":\"personal\"}],\"entityRanges\":[],\"data\":{}}],\"entityMap\":{}}";
            assertEquals(HttpStatus.OK, noteService.saveNote(noteTitle, testUsername, newBody), "Saving a note should work.");
            Optional<Note> optionalNote = noteService.getNote(noteTitle, testUsername);
            assertTrue(optionalNote.isPresent(), "After saving a note, that note should still exist.");
            Note note = optionalNote.get();
            assertEquals(newBody, note.getBody(), "After saving a note, the body should be updated to match the new body.");
        }

    }

    @DisplayName("Test edge cases for note titles")
    @Test
    public void testNameEdges() {
        assertEquals(HttpStatus.PRECONDITION_FAILED, noteService.createNote(null, testUsername), "Creating a note with a null title should fail");
        final String noteTitle = RandomStringUtils.randomAlphanumeric(200);
        assertEquals(HttpStatus.OK, noteService.createNote(noteTitle, testUsername), "Creating a note with a 200 character name should work.");
        assertTrue(noteService.userHasNote(noteTitle, testUsername), "A user that creates a note should have a note with that title.");
        assertEquals(HttpStatus.OK, noteService.deleteNote(noteTitle, testUsername), "Deleting a note should not throw an exception.");
        final String longTitle = RandomStringUtils.randomAlphanumeric(1000);
        assertEquals(HttpStatus.PRECONDITION_FAILED, noteService.createNote(longTitle, testUsername), "Creating a note with a 1000 character body should fail.");
        assertEquals(HttpStatus.PRECONDITION_FAILED, noteService.createNote(noteTitle, null), "Creating a note with a null author should fail.");
        assertEquals(HttpStatus.PRECONDITION_FAILED, noteService.createNote(null, null), "Creating a note with a null author and title should fail.");
    }

    /**
     * this is isn't in the inner class because it renames the note, and i'd also like to test deleting renamed notes here
     */
    @DisplayName("Test renaming notes")
    @Test
    public void testRenaming() {
        final String originalTitle = RandomStringUtils.randomAlphanumeric(12);
        assertEquals(HttpStatus.OK, noteService.createNote(originalTitle, testUsername), "Creating a standard note should work.");

        final String newTitle = RandomStringUtils.randomAlphanumeric(11);
        assertEquals(HttpStatus.OK, noteService.renameNote(originalTitle, newTitle, testUsername), "Renaming a note to a valid name should work.");
        assertTrue(() -> noteService.getNote(originalTitle, testUsername).isEmpty(), "After renaming a note, getting the old title should not work.");
        Optional<Note> optionalRenamedNote = noteService.getNote(newTitle, testUsername);
        assertTrue(optionalRenamedNote.isPresent(), "After renaming a note, the getting a note by the renamed title should work.");
        Note renamedNote = optionalRenamedNote.get();
        assertEquals(testUsername, renamedNote.getAuthor(), "After renaming a note, the author should persist.");
        assertEquals(newTitle, renamedNote.getTitle(), "After renaming a note, the new title of the note should be equal to the assigned title.");
        assertEquals(NoteUtils.getBlankNoteBody(), renamedNote.getBody(), "After renaming a note, the body of the note should persist.");
        assertEquals(HttpStatus.OK, noteService.deleteNote(newTitle, testUsername), "After renaming a note, deleting a note with the new title should work.");
        assertEquals(0, noteService.getNoteCount(testUsername), "After deleting the user's only note, they should have no notes.");
    }

    /**
     * not in nested function so that it can test behavior before the note is created
     */
    @DisplayName("Test user has note")
    @Test
    public void testUserHasNote() {
        final String noteTitle = RandomStringUtils.randomAlphanumeric(12);
        assertFalse(noteService.userHasNote(noteTitle, testUsername), "Before a user has any notes, they should not have a note with a specified title.");
        assertEquals(HttpStatus.OK, noteService.createNote(noteTitle, testUsername), "Creating a note should work.");
        assertTrue(noteService.userHasNote(noteTitle, testUsername), "After creating a note, that note should be found.");
        assertEquals(HttpStatus.OK, noteService.deleteNote(noteTitle, testUsername), "Deleting a note should work.");
        assertFalse(noteService.userHasNote(noteTitle, testUsername), "After deleting a note, that note should note be found.");
    }

    //todo add test for duplicating the same note twice (second name should be name (copy) (copy))

}
