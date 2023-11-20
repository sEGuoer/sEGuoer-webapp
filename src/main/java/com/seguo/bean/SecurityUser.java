package com.seguo.bean;


import com.seguo.entity.Role;
import com.seguo.entity.Permission;
import com.seguo.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {
    @Getter
    private final User user;
    public SecurityUser(User user) {
        this.user = user;
    }

    String password;  // 密码
    String username;  // 用户名
    boolean accountNonExpired;   // 是否未过期
    boolean accountNonLocked;   // 是否未被锁定
    boolean credentialsNonExpired;  // 密码是否未过期
    boolean enabled;  // 账号是否可用
    Collection<? extends GrantedAuthority> authorities;  // 用户的权限集合

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> permissions = new ArrayList<>();
        List<Role> roles = this.user.getRoles();
        for (Role role : roles) {
            permissions.add("ROLE_" + role.getName());
            for (Permission permission : role.getPermissions()) {
                permissions.add(permission.getName());
            }
        }
        return AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", permissions));
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;   // 暂时未用到，直接返回true，表示账户未过期
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;   // 暂时未用到，直接返回true，表示账户未被锁定
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;   // 暂时未用到，直接返回true，表示账户密码未过期
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEnabled();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

}