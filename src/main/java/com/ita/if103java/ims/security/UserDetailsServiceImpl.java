package com.ita.if103java.ims.security;

import com.ita.if103java.ims.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(Optional.ofNullable(userDao.findByEmail(username))
            .orElseThrow(() -> {
                throw new UsernameNotFoundException("Error during obtain user with this username");
            }));

    }
}
