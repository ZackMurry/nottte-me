package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.users.UserDao;
import com.zackmurry.nottteme.entities.User;
import com.zackmurry.nottteme.models.sharing.LinkShareStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LinkShareService linkShareService;

    @Autowired
    private PasswordEncoder encoder;

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
     * @implNote don't ever expose to user really
     * @param username username to get account from
     * @return Optional of user
     */
    private Optional<User> getUserByUsernameIncludePassword(String username) {
        return userDao.getUserByUsernameIncludePassword(username);
    }

    /**
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

    public HttpStatus updatePassword(String username, String oldPassword, String newPassword) {
        if(oldPassword.equals(newPassword)) {
            return HttpStatus.NOT_MODIFIED;
        }
        if(newPassword.length() < 8) return HttpStatus.LENGTH_REQUIRED;
        Optional<User> optionalUser = getUserByUsernameIncludePassword(username);
        if(optionalUser.isEmpty()) {
            return HttpStatus.NOT_MODIFIED;
        }
        User user = optionalUser.get();

        //check if the current password matches the inputted "old" password
        if(!encoder.matches(oldPassword, user.getPassword())) {
            return HttpStatus.FORBIDDEN;
        }

        String encodedNewPassword = encoder.encode(newPassword);
        System.out.println("new encoded password: " + encodedNewPassword);
        return userDao.updatePassword(username, encodedNewPassword);
    }
}
