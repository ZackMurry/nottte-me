package com.zackmurry.nottteme.dao.link_shares;

import com.zackmurry.nottteme.dao.notes.NoteDao;
import com.zackmurry.nottteme.models.LinkShare;
import com.zackmurry.nottteme.models.LinkShareRequest;
import com.zackmurry.nottteme.models.LinkShareStatus;
import com.zackmurry.nottteme.models.ShareAuthority;
import javassist.NotFoundException;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Repository
public class LinkShareDataAccessService implements LinkShareDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private NoteDao noteDao;

    @Autowired
    public LinkShareDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }

    @Override
    public HttpStatus createShareableLink(LinkShareRequest request) {
        String sql = "INSERT INTO link_shares (author, note_id, authority) VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, request.getAuthor());
            preparedStatement.setLong(2, noteDao.getIdByTitleAndAuthor(request.getName(), request.getAuthor()));
            preparedStatement.setString(3, request.getAuthority()); //todo not working
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    @Override
    public HttpStatus incrementShareableLinkUsages(UUID id) {
        String sql = "UPDATE link_shares SET times_used = times_used + 1 WHERE id=? LIMIT 1";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public LinkShare getLinkShareById(UUID id) throws NotFoundException, SQLException {
        String sql = "SELECT author, note_id, authority, status, times_used FROM link_shares WHERE id=? LIMIT 1";

        try {

            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) throw new NotFoundException("Cannot find link share with id " + id);

            return new LinkShare(
                    id,
                    resultSet.getString(1), //author
                    resultSet.getLong(2), //note id
                    ShareAuthority.valueOf(resultSet.getString(3)), //authority
                    LinkShareStatus.valueOf(resultSet.getString(4)), //status
                    resultSet.getInt(5) //times used
            );
        } catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
