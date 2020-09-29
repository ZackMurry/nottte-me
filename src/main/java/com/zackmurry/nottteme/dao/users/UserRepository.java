package com.zackmurry.nottteme.dao.users;

import com.zackmurry.nottteme.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);

}
