package com.threadly.notification.auth;

import com.threadly.notification.core.domain.user.UserStatus;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class JwtAuthenticationUser implements UserDetails {

  private final String userId;
  private final UserStatus userStatus;
  private final Collection<? extends GrantedAuthority> authorities;

  public JwtAuthenticationUser(String userId, UserStatus userStatus,
      Collection<? extends GrantedAuthority> authorities) {
    this.userId = userId;
    this.userStatus = userStatus;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    //userId는 식별자
    return userId;
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
    return true;
  }
}
