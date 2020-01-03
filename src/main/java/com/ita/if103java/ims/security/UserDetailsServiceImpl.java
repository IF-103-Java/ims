package com.ita.if103java.ims.security;

import com.ita.if103java.ims.dao.AccountDao;
import com.ita.if103java.ims.dao.AccountTypeDao;
import com.ita.if103java.ims.dao.UserDao;
import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDao userDao;
    private AccountDao accountDao;
    private AccountTypeDao accountTypeDao;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao, AccountDao accountDao, AccountTypeDao accountTypeDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.accountTypeDao = accountTypeDao;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByEmail(username);
        AccountType type = accountTypeDao.findById(accountDao.findById(user.getAccountId()).getTypeId());
        return new UserDetailsImpl(user, type);
    }
}
