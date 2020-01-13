package com.ita.if103java.ims.security;

import com.ita.if103java.ims.entity.AccountType;
import com.ita.if103java.ims.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class UserDetailsImpl implements UserDetails {

    private User user;
    private AccountType accountType;

    public UserDetailsImpl(User user, AccountType accountType) {
        this.user = user;
        this.accountType = accountType;
    }

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public UserDetailsImpl() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Stream<String> authoritiesStream = Stream.of(
            user.getRole().name(),
            accountType.isItemStorageAdvisor() ? "ITEM_STORAGE_ADVISOR" : null,
            accountType.isDeepWarehouseAnalytics() ? "DEEP_WAREHOUSE_ANALYTICS" : null
        );
        return authoritiesStream.filter(Objects::nonNull).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public User getUser() {
        return user;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
