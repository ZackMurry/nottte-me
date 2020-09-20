package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.user.UserDao;
import com.zackmurry.nottteme.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public boolean createUserAccount(String username, String password, String email) {
        return userDao.createUserAccount(username, password, email);
    }

    public boolean usernameExists(String username) {
        return userDao.accountExists(username);
    }

    public Optional<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    /**
     * deletes account from users table -- *notably* keeps notes
     * @param username username of account to delete
     * @return response status
     */
    public HttpStatus deleteAccount(String username) {
        return userDao.deleteAccount(username);
    }

    public HttpStatus updateEmail(String username, String email) {
        return userDao.updateEmail(username, email);
    }
}
