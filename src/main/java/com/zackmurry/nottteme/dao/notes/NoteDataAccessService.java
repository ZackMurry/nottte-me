package com.zackmurry.nottteme.dao.notes;

import com.zackmurry.nottteme.models.Note;
import javassist.NotFoundException;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * used for accessing and updating data about notes
 */
@Service
public final class NoteDataAccessService implements NoteDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NoteDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public ResponseEntity<HttpStatus> updateNote(String title, String author, String content) {
        String sql = "UPDATE notes SET body = ? WHERE title=? AND author=?";
        try {
            jdbcTemplate.execute(
                    sql,
                    content,
                    title,
                    author
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (SQLException e) {
            //this shouldn't really happen
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * gets a note's id by its title and author
     *
     * @param title title of note to get
     * @param author person who wrote the note
     * @return id of note
     * @throws IllegalArgumentException thrown upon a SQLException
     * @throws IllegalStateException thrown when database has 0 or 2+ notes with the given name and author
     */
    @Override
    public long getIdByTitleAndAuthor(String title, String author) throws IllegalArgumentException, IllegalStateException {
        String sql = "SELECT id FROM notes WHERE title=? AND author=?";

        try {
            List<Note> notes = jdbcTemplate.query(
                    sql,
                    resultSet -> new Note(
                            resultSet.getLong(1), //id
                            resultSet.getString(2), //author
                            resultSet.getString(3), //title
                            resultSet.getString(4) //body
                    ),
                    title,
                    author
            );
            if(notes.size() < 1) {
                throw new IllegalStateException("There should be a note with the given title and author; title: " + title + ", author: " + author);
            } else if(notes.size() > 1) {
                throw new IllegalStateException("There should never be two notes with the same titles and authors; titles: " + title + ", authors: " + author);
            }
            return notes.get(0).getId();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to perform SQL query", e);
        }

    }

    @Override
    public ResponseEntity<HttpStatus> createNote(String title, String body, String author) {
        String sql = "INSERT INTO notes (author, title, body) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.execute(
                    sql,
                    author,
                    title,
                    body
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public boolean noteWithNameExists(String title) {
        String sql = "SELECT EXISTS (SELECT title FROM notes WHERE title=?)";

        try {
            List<?> l = jdbcTemplate.queryForList(
                    sql,
                    title
            );
            //l.get(0) returns "{exists=[t/f]}", so getting the char at index 8 gets either t or f (for true r false)
            //if it's equal to 't', return true, else return false
            return l.get(0).toString().charAt(8) == 't';
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean userHasNote(String title, String username) {
        String sql = "SELECT EXISTS (SELECT title FROM notes WHERE title=? AND author=?)";

        try {
            List<?> l = jdbcTemplate.queryForList(
                    sql,
                    title,
                    username
            );
            //l.get(0) returns "{exists=[t/f]}", so getting the char at index 8 gets either t or f (for true r false)
            //if it's equal to 't', return true, else return false
            return l.get(0).toString().charAt(8) == 't';
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * gets body from a note
     *
     * @param title title of note
     * @param author author of note
     * @return body of note
     */
    @Override
    public String getRawNote(String title, String author) {
        String sql = "SELECT body FROM notes WHERE title=? AND author=? LIMIT 1";

        try {
            List<String> rawList = jdbcTemplate.query(
                    sql,
                    resultSet -> resultSet.getString(1),
                    title,
                    author
            );
            if(rawList.size() != 1) throw new IllegalStateException("Raw note list should only have one element. Instead, it has " + rawList.size());
            return rawList.get(0);
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public List<Note> getNotesByUser(String username) {
        String sql = "SELECT * FROM notes WHERE author=?";

        try {
            return jdbcTemplate.query(
                    sql,
                    resultSet -> new Note(
                            resultSet.getLong(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4)
                    ),
                    username
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public ResponseEntity<HttpStatus> deleteNote(String title, String username) throws NotFoundException {
        String sql = "DELETE FROM notes WHERE title=? AND author=?";

        if(!userHasNote(title, username)) {
            throw new NotFoundException("Note " + title + " with author " + username + " not found.");
        }

        try {
            jdbcTemplate.execute(
                    sql,
                    title,
                    username
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<HttpStatus> renameNote(String oldTitle, String newTitle, String username) throws NotFoundException {
        //checking if user has a note with that name
        if(!userHasNote(oldTitle, username)) {
            throw new NotFoundException("Cannot find note with title " + oldTitle + " by user " + username + ".");
        }

        String sql = "UPDATE notes SET title=? WHERE title=? AND author=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    newTitle,
                    oldTitle,
                    username
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }


}
