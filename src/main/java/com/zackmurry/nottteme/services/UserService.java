package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.users.UserDao;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.sharing.LinkShareStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LinkShareService linkShareService;

    public boolean createUserAccount(String username, String password, String email) {
        return userDao.createUserAccount(username, password, email);
    }

    public boolean accountExists(String username) {
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
        if(linkShareService.setStatusOfLinkSharesByUser(username, LinkShareStatus.ACCOUNT_DELETED).isError()) return HttpStatus.INTERNAL_SERVER_ERROR;
        return userDao.deleteAccount(username);
    }

    public HttpStatus updateEmail(String username, String email) {
        return userDao.updateEmail(username, email);
    }

}
