package com.zackmurry.nottteme.dao.user;

import com.zackmurry.nottteme.entities.User;

import java.util.Optional;

public interface UserDao {

    boolean createUserAccount(String username, String password);

    boolean accountExists(String username);

    Optional<User> getUserByUsername(String username);

}
