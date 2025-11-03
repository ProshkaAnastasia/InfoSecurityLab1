package com.security.api.service;
import com.security.api.entity.User;import com.security.api.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;import org.springframework.security.core.userdetails.*;import org.springframework.stereotype.Service;import java.util.stream.Collectors;
@Service public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository repo; public UserDetailsServiceImpl(UserRepository r){this.repo=r;}
  @Override public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
    User u=repo.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
    return new org.springframework.security.core.userdetails.User(u.getUsername(),u.getPassword(),
      u.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())); }
}