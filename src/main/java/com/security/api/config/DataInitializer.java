package com.security.api.config;
import com.security.api.entity.User;
import com.security.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Configuration 
public class DataInitializer {
  @Bean CommandLineRunner 
  init(UserRepository repo, PasswordEncoder enc) {

    return args->{
      if(repo.count()==0){
        User u = new User();
        u.setUsername("user");
        u.setPassword(enc.encode("password123"));
        u.setRoles(Set.of("ROLE_USER"));
        repo.save(u);
        User a=new User();
        a.setUsername("admin");
        a.setPassword(enc.encode("admin123"));
        a.setRoles(Set.of("ROLE_USER","ROLE_ADMIN"));
        repo.save(a);
      }
    };
  }
}