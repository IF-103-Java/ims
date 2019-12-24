package com.ita.if103java.ims.security;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDao userDao;
    private AccountDao accountDao;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        Optional.ofNullable(user = userDao.findByEmail(username))
            .orElseThrow(() -> {
                throw new UsernameNotFoundException("Error during obtain user with this username");
            });
        AccountType type = accountDao.findById(user.getId()).getType();

        return new UserDetailsImpl(user, type);
    }
}
