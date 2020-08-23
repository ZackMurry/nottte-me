package com.zackmurry.nottteme.repository;

import com.zackmurry.nottteme.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);

}
