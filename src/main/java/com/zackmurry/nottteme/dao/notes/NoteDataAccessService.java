package com.zackmurry.nottteme.dao.notes;

import com.zackmurry.nottteme.models.Note;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Service
public class NoteDataAccessService implements NoteDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NoteDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public ResponseEntity<HttpStatus> updateNote(String title, String author, String content) {
        String sql = "UPDATE notes SET body = ? WHERE title=? AND author=?";
        author = "test"; //todo temp

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
                    body,
                    author
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }


}
