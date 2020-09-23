package com.zackmurry.nottteme.dao.notes;

import com.zackmurry.nottteme.models.Note;
import javassist.NotFoundException;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
    public HttpStatus updateNote(String title, String author, String content) {
        String sql = "UPDATE notes SET body = ? WHERE title=? AND author=?";
        try {
            jdbcTemplate.execute(
                    sql,
                    content,
                    title,
                    author
            );
            updateLastModified(title, author);
            return HttpStatus.OK;
        } catch (SQLException e) {
            //this shouldn't really happen
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
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
            List<Long> ids = jdbcTemplate.query(
                    sql,
                    resultSet -> resultSet.getLong(1),
                    title,
                    author
            );
            if(ids.size() < 1) {
                throw new IllegalStateException("There should be a note with the given title and author; title: " + title + ", author: " + author);
            } else if(ids.size() > 1) {
                throw new IllegalStateException("There should never be two notes with the same titles and authors; titles: " + title + ", authors: " + author);
            }
            return ids.get(0);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to perform SQL query", e);
        }

    }

    @Override
    public HttpStatus createNote(String title, String body, String author) {
        if(userHasNote(title, author)) {
            return HttpStatus.PRECONDITION_FAILED;
        }

        String sql = "INSERT INTO notes (author, title, body) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.execute(
                    sql,
                    author,
                    title,
                    body
            );
            return HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
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
                            resultSet.getString(4),
                            resultSet.getTimestamp(5)
                    ),
                    username
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public HttpStatus deleteNote(String title, String username) throws NotFoundException {
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
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

    }

    @Override
    public HttpStatus renameNote(String oldTitle, String newTitle, String username) throws NotFoundException, IllegalArgumentException {
        //checking if user has a note with that name
        if(!userHasNote(oldTitle, username)) {
            throw new NotFoundException("Cannot find note with title " + oldTitle + " by user " + username + ".");
        }
        if(userHasNote(newTitle, username)) {
            throw new IllegalArgumentException("User already has a note with title " + newTitle + ".");
        }

        String sql = "UPDATE notes SET title=? WHERE title=? AND author=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    newTitle,
                    oldTitle,
                    username
            );
            updateLastModified(newTitle, username);
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

    }

    @Override
    public int getNoteCount(String username) {
        String sql = "SELECT COUNT(*) FROM notes WHERE author=?";

        try {
            return jdbcTemplate.queryForInt(
                    sql,
                    username
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return -1;
        }

    }

    @Override
    public HttpStatus deleteNotesByAuthor(String author) {
        String sql = "DELETE FROM notes WHERE author=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    author
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

    }

    @Override
    public List<String> getRawNotesByIdList(List<Long> noteIds) {
        String questionMarks = String.join(",", Collections.nCopies(noteIds.size(), "?"));

        String sql = String.format("SELECT body FROM notes WHERE id IN (%s)", questionMarks);

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            for (int i = 1; i <= noteIds.size(); i++) {
                preparedStatement.setLong(i, noteIds.get(i-1));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> bodies = new ArrayList<>();
            int index = 0;
            while(resultSet.next()) {
                bodies.add(resultSet.getString(++index));
            }
            return bodies;
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    @Override
    public List<Note> getNotesByIdList(List<Long> noteIds) {
        String questionMarks = String.join(",", Collections.nCopies(noteIds.size(), "?"));

        String sql = String.format("SELECT * FROM notes WHERE id IN (%s)", questionMarks);

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            for (int i = 1; i <= noteIds.size(); i++) {
                preparedStatement.setLong(i, noteIds.get(i-1));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Note> notes = new ArrayList<>();
            while(resultSet.next()) {
                notes.add(
                        new Note(
                              resultSet.getLong(1), //id
                              resultSet.getString(2), //author
                              resultSet.getString(3), //title
                              resultSet.getString(4), //body
                              resultSet.getTimestamp(5) //last modified
                        )
                );
            }
            return notes;
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public HttpStatus updateLastModified(String title, String author) {
        String sql = "UPDATE notes SET last_modified = ? WHERE title=? AND author=?";

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setTimestamp(1, currentTime);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, author);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }
    }

}
