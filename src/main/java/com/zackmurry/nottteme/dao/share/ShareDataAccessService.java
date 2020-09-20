package com.zackmurry.nottteme.dao.share;

import com.zackmurry.nottteme.dao.notes.NoteDao;
import com.zackmurry.nottteme.dao.user.UserDao;
import com.zackmurry.nottteme.exceptions.UnauthorizedException;
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
        if(!noteDao.userHasNote(author, title)) return HttpStatus.NOT_FOUND;
        if(!userDao.accountExists(author) || !userDao.accountExists(recipient)) return HttpStatus.NOT_ACCEPTABLE;

        long noteId = noteDao.getIdByTitleAndAuthor(title, author);

        //if share is already there, return not modified
        if(noteIsSharedWithUser(noteId, recipient)) return HttpStatus.NOT_MODIFIED;

        String sql = "INSERT INTO shares (note_id, shared_username) VALUES (?, ?)";

        try {
            jdbcTemplate.execute(
                    sql,
                    noteId,
                    recipient
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

    }

    /**
     *
     * @param username author name
     * @param title title of note
     * @return list of users that it's shared with
     * @throws NotFoundException if note not found
     */
    @Override
    public List<String> getSharesOfNote(String username, String title) throws NotFoundException {
        if(!noteDao.userHasNote(title, username)) throw new NotFoundException("User must have note to share it.");

        long noteId = noteDao.getIdByTitleAndAuthor(title, username);

        String sql = "SELECT shared_username FROM shares WHERE note_id=?";

        try {

            //using prepared statements because otherwise you can't really specify data types
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> out = new ArrayList<>();
            while(resultSet.next()) {
                out.add(resultSet.getString(1));
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
    public String getSharedNote(String title, String author, String username) throws NotFoundException, UnauthorizedException {
        if(!noteDao.userHasNote(title, author)) throw new NotFoundException("Unable to find note " + title + " by author " + author + ".");

        long noteId = noteDao.getIdByTitleAndAuthor(title, author);
        if(!noteIsSharedWithUser(noteId, username)) throw new UnauthorizedException(username + " does not have access to note " + title + " by author " + author + ".");

        return noteDao.getRawNote(title, author);
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
    public HttpStatus unshareNoteWithUser(String username, String title, String recipient) throws NotFoundException {
        if(!noteDao.userHasNote(title, username)) throw new NotFoundException("Cannot find note " + title + " by user " + username + ".");
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
            return HttpStatus.BAD_REQUEST;
        }

    }
}