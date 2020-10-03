package com.zackmurry.nottteme.dao.users;

import com.zackmurry.nottteme.entities.User;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * used for accessing and updating data about users and their preferences
 * todo only allow one attribute of each attribute type per shortcut (literally doesn't make sense to override itself and makes it easier to index)
 */
@Service
public final class UserDataAccessService implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDataAccessService(DataSource dataSource) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(dataSource.getConnection());
    }


    @Override
    public boolean createUserAccount(String username, String password, String email) {
        if(accountExists(username)) return false;
        if(email == null) email = "";

        String userSql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        //not sure if the best strategy is to run this in here,
        //but it abstracts all the logic into this method so it is what it is
        String shortcutSql = "INSERT INTO shortcuts (username) VALUES (?)";

        try {
            jdbcTemplate.execute(
                    userSql,
                    username,
                    password,
                    email
            );

            //initializing shortcuts
            jdbcTemplate.execute(
                    shortcutSql,
                    username
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean accountExists(String username) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE username=?)";

        try {
            return jdbcTemplate.queryForBoolean(
                    sql,
                    username
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=? LIMIT 1";

        try {
            List<User> list = jdbcTemplate.query(
                    sql,
                    resultSet -> new User(
                            resultSet.getString(1), //username
                            "secured", //password hidden because it's pretty useless when it's encrypted
                            resultSet.getString(3) //email
                            ),
                    username
            );
            return Optional.of(list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public HttpStatus deleteAccount(String username) {
        if(!accountExists(username)) {
            return HttpStatus.NOT_FOUND;
        }

        String sql = "DELETE FROM users WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

    }

    @Override
    public HttpStatus updateEmail(String username, String email) {
        if(!accountExists(username)) {
            return HttpStatus.NOT_FOUND;
        }

        String sql = "UPDATE users SET email = ? WHERE username=?";

        try {
            jdbcTemplate.execute(
                    sql,
                    email,
                    username
            );
            return HttpStatus.OK;
        } catch(SQLException e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

    }


}
