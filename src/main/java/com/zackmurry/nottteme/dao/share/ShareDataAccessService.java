package com.zackmurry.nottteme.dao.share;

import com.zackmurry.nottteme.dao.notes.NoteDao;
import com.zackmurry.nottteme.dao.users.UserDao;
import com.zackmurry.nottteme.exceptions.UnauthorizedException;
import com.zackmurry.nottteme.models.Note;
import com.zackmurry.nottteme.models.sharing.NoteShare;
import javassist.NotFoundException;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShareDataAccessService implements ShareDao {

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private UserDao userDao;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ShareDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }



    @Override
    public HttpStatus shareNoteWithUser(String author, String title, String recipient) {
        if(!noteDao.userHasNote(title, author)) return HttpStatus.NOT_FOUND;
        if(!userDao.accountExists(author) || !userDao.accountExists(recipient)) return HttpStatus.NOT_ACCEPTABLE;

        long noteId = noteDao.getIdByTitleAndAuthor(title, author);

        //if share is already there, return not modified
        if(noteIsSharedWithUser(noteId, recipient)) return HttpStatus.NOT_MODIFIED;

        String sql = "INSERT INTO shares (note_id, shared_username) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            preparedStatement.setString(2, recipient);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    @Override
    public HttpStatus shareNoteWithUser(long id, String recipient) {
        //if share is already there, return not modified
        if(noteIsSharedWithUser(id, recipient)) return HttpStatus.NOT_MODIFIED;

        String sql = "INSERT INTO shares (note_id, shared_username) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, recipient);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     *
     * @param username author name
     * @param title title of note
     * @return list of users that it's shared with
     */
    @Override
    public List<NoteShare> getSharesOfNote(String username, String title) {
        if(!noteDao.userHasNote(title, username)) return new ArrayList<>();
        long noteId = noteDao.getIdByTitleAndAuthor(title, username);

        String sql = "SELECT * FROM shares WHERE note_id=?";

        try {

            //using prepared statements because otherwise you can't really specify data types
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<NoteShare> out = new ArrayList<>();
            while(resultSet.next()) {
                out.add(new NoteShare(
                        resultSet.getLong("id"),
                        resultSet.getLong("note_id"),
                        resultSet.getString("shared_username"),
                        resultSet.getBoolean("can_share")
                ));
            }
            return out;
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    @Override
    public boolean noteIsSharedWithUser(String title, String author, String recipient) {
        if(!noteDao.userHasNote(title, author)) return false;

        long noteId = noteDao.getIdByTitleAndAuthor(title, author);

        return noteIsSharedWithUser(noteId, recipient);

    }

    @Override
    public String getRawSharedNote(String title, String author, String username) throws NotFoundException, UnauthorizedException {
        if(!noteDao.userHasNote(title, author)) throw new NotFoundException("Unable to find note " + title + " by author " + author + ".");

        long noteId = noteDao.getIdByTitleAndAuthor(title, author);
        if(!noteIsSharedWithUser(noteId, username)) throw new UnauthorizedException(username + " does not have access to note " + title + " by author " + author + ".");

        return noteDao.getRawNote(title, author, false);
    }

    /**
     * because this is a private method, no other checks are performed,
     * since they've likely already been done
     *
     * @param noteId id of note in question
     * @param recipient name of user in question
     * @return whether or not the recipient user has access to the note
     */
    private boolean noteIsSharedWithUser(long noteId, String recipient) {
        String sql = "SELECT EXISTS (SELECT 1 FROM shares WHERE note_id=? AND shared_username=?)";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            preparedStatement.setString(2, recipient);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next(); //ResultSet starts before the first row
            return resultSet.getBoolean(1);
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public HttpStatus unshareNoteWithUser(String username, String title, String recipient) {
        if(!noteDao.userHasNote(title, username)) return HttpStatus.NOT_FOUND;
        long noteId = noteDao.getIdByTitleAndAuthor(title, username);
        if(!noteIsSharedWithUser(noteId, recipient)) return HttpStatus.NOT_MODIFIED;

        String sql = "DELETE FROM shares WHERE note_id=? AND shared_username=?";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            preparedStatement.setString(2, recipient);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    @Override
    public List<Long> getNoteIdsSharedWithUser(String username) {

        String sql = "SELECT note_id FROM shares WHERE shared_username=?";

        try {
            return jdbcTemplate.query(
                    sql,
                    resultSet -> resultSet.getLong("note_id"),
                    username
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    @Override
    public Optional<Note> getSharedNote(String title, String author, String username) throws UnauthorizedException {
        if(!noteDao.userHasNote(title, author)) return Optional.empty();

        long noteId = noteDao.getIdByTitleAndAuthor(title, author);
        if(!noteIsSharedWithUser(noteId, username)) throw new UnauthorizedException(username + " does not have access to note " + title + " by author " + author + ".");

        return noteDao.getNote(title, author);
    }

    @Override
    public Optional<NoteShare> getShareOfNote(String title, String author, String sharedUsername) {
        long noteId = noteDao.getIdByTitleAndAuthor(title, author);
        String sql = "SELECT * FROM shares WHERE note_id=? LIMIT 1";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if(resultSet.isAfterLast()) return Optional.empty();
            return Optional.of(
                    new NoteShare(
                            resultSet.getLong("id"),
                            resultSet.getLong("note_id"),
                            resultSet.getString("shared_username"),
                            resultSet.getBoolean("can_share")
                    )
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public HttpStatus setUserCanShareNote(String title, String author, String sharedUsername, boolean newValue) {
        long noteId = noteDao.getIdByTitleAndAuthor(title, author);
        String sql = "UPDATE shares SET can_share = ? WHERE note_id=?";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setBoolean(1, newValue);
            preparedStatement.setLong(2, noteId);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
