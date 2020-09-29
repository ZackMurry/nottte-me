package com.zackmurry.nottteme.services;

import com.zackmurry.nottteme.dao.users.UserRepository;
import com.zackmurry.nottteme.entities.NottteUserPrincipal;
import com.zackmurry.nottteme.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class NottteUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new NottteUserPrincipal(user); //also might want to create a custom user class
    }

}
