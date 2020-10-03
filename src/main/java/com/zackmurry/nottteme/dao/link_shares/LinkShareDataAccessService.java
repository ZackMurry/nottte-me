package com.zackmurry.nottteme.dao.link_shares;

import com.zackmurry.nottteme.dao.notes.NoteDao;
import com.zackmurry.nottteme.models.sharing.LinkShare;
import com.zackmurry.nottteme.models.sharing.LinkShareRequest;
import com.zackmurry.nottteme.models.sharing.LinkShareStatus;
import com.zackmurry.nottteme.models.sharing.ShareAuthority;
import javassist.NotFoundException;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        long noteId = noteDao.getIdByTitleAndAuthor(request.getName(), request.getAuthor());

        if(shareableLinkExists(noteId)) {
            Optional<LinkShare> optionalLinkShare = getLinkShare(noteId, ShareAuthority.valueOf(request.getAuthority()));

            if(optionalLinkShare.isPresent()) {
                LinkShare linkShare = optionalLinkShare.get();
                if(linkShare.getStatus().equals(LinkShareStatus.ACTIVE)) {
                    return HttpStatus.NOT_MODIFIED;
                } else {
                    return setShareableLinkStatus(linkShare.getId(), LinkShareStatus.ACTIVE);
                }
            }
        }

        String sql = "INSERT INTO link_shares (author, note_id, authority) VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, request.getAuthor());
            preparedStatement.setLong(2, noteId);
            preparedStatement.setString(3, request.getAuthority());
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    @Override
    public boolean shareableLinkExists(long noteId) {
        String sql = "SELECT 1 FROM link_shares WHERE note_id=? LIMIT 1";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public HttpStatus incrementShareableLinkUsages(UUID id) {
        String sql = "UPDATE link_shares SET times_used = times_used + 1 WHERE id=?";

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

    @Override
    public Optional<LinkShare> getLinkShare(long noteId, ShareAuthority authority) {
        String sql = "SELECT author, id, status, times_used FROM link_shares WHERE note_id=? AND authority=? LIMIT 1";

        try {

            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            preparedStatement.setString(2, authority.getAuthority());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(
                    new LinkShare(
                        UUID.fromString(resultSet.getString(2)), //id
                        resultSet.getString(1), //author
                        noteId, //note id
                        authority, //authority
                        LinkShareStatus.valueOf(resultSet.getString(3)), //status
                        resultSet.getInt(4) //times used
                )
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public Optional<LinkShare> getLinkShare(long noteId) {
        String sql = "SELECT author, id, authority, status, times_used FROM link_shares WHERE note_id=? LIMIT 1";

        try {

            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(
                    new LinkShare(
                            UUID.fromString(resultSet.getString(2)), //id
                            resultSet.getString(1), //author
                            noteId, //note id
                            ShareAuthority.valueOf(resultSet.getString(3)), //authority
                            LinkShareStatus.valueOf(resultSet.getString(4)), //status
                            resultSet.getInt(5) //times used
                    )
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

    @Override
    public String getAuthorById(UUID id) throws NotFoundException, SQLException {
        String sql = "SELECT author FROM link_shares WHERE id=? LIMIT 1";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) throw new NotFoundException("Cannot find link share with id " + id);

            return resultSet.getString(1);
        } catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public HttpStatus setShareableLinkStatus(UUID id, LinkShareStatus status) {
        String sql = "UPDATE link_shares SET status = ? WHERE id=? LIMIT 1";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, status.getStatus());
            preparedStatement.setObject(2, id);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public List<LinkShare> getLinkSharesByNoteId(long noteId) {
        String sql = "SELECT author, id, authority, status, times_used FROM link_shares WHERE note_id=?";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setLong(1, noteId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<LinkShare> linkShares = new ArrayList<>();
            while(resultSet.next()) {
                linkShares.add(
                        new LinkShare(
                                UUID.fromString(resultSet.getString(2)),
                                resultSet.getString(1), //author
                                noteId, //note id
                                ShareAuthority.valueOf(resultSet.getString(3)), //authority
                                LinkShareStatus.valueOf(resultSet.getString(4)), //status
                                resultSet.getInt(5) //times used
                        )
                );
            }
            return linkShares;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    @Override
    public HttpStatus setStatusOfLinkSharesOfNote(long noteId, LinkShareStatus newStatus) {
        String sql = "UPDATE link_shares SET status = ? WHERE note_id=?";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, newStatus.getStatus());
            preparedStatement.setLong(2, noteId);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    @Override
    public HttpStatus setStatusOfLinkSharesByUser(String username, LinkShareStatus newStatus) {
        String sql = "UPDATE link_shares SET status = ? WHERE author=?";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, newStatus.getStatus());
            preparedStatement.setString(2, username);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public HttpStatus updateSharableLink(UUID id, LinkShare newLinkShare) {
        String sql = "UPDATE link_shares SET status = ?, authority = ? WHERE id=?";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, newLinkShare.getStatus().getStatus());
            preparedStatement.setString(2, newLinkShare.getAuthority().getAuthority());
            preparedStatement.setObject(3, id);
            preparedStatement.execute();
            return HttpStatus.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public HttpStatus deleteLinkShareById(UUID id) {
        String sql = "DELETE FROM link_shares WHERE id=?";

        try {
            PreparedStatement preparedStatement = jdbcTemplate.getConnection().prepareStatement(sql);
            preparedStatement.setObject(1, id);
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
