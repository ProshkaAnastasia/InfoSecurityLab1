package com.security.api.service;
import com.security.api.dto.*;import com.security.api.entity.User;import com.security.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.stereotype.Service;import java.util.Set;
@Service public class UserService {
  private final UserRepository repo; private final PasswordEncoder enc; private final JwtService jwt;
  public UserService(UserRepository r,PasswordEncoder e,JwtService j){this.repo=r;this.enc=e;this.jwt=j;}
  public AuthResponse register(UserRegistrationRequest req){if(repo.existsByUsername(req.getUsername())) throw new RuntimeException("exists");
    User u=new User();u.setUsername(req.getUsername());u.setPassword(enc.encode(req.getPassword()));u.setEmail(req.getEmail());u.setRoles(Set.of("ROLE_USER"));repo.save(u);
    return new AuthResponse(null,"User registered successfully",u.getUsername());}
  public AuthResponse authenticate(AuthRequest req){User u=repo.findByUsername(req.getUsername()).orElseThrow();
    if(!enc.matches(req.getPassword(),u.getPassword())) throw new RuntimeException("Bad credentials");
    String token=jwt.generateToken(org.springframework.security.core.userdetails.User.withUsername(u.getUsername()).password(u.getPassword()).authorities(u.getRoles().toArray(new String[0])).build());
    return new AuthResponse(token,"Authentication successful",u.getUsername()); }
}