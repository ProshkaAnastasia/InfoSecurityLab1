package com.security.api.entity;
import jakarta.persistence.*;import java.util.*;
@Entity @Table(name="users")
public class User { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(unique=true,nullable=false) private String username; @Column(nullable=false) private String password; private String email;
  @ElementCollection(fetch=FetchType.EAGER) private Set<String> roles=new HashSet<>();
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public String getUsername(){return username;} public void setUsername(String u){this.username=u;}
  public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
  public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
  public Set<String> getRoles(){return roles;} public void setRoles(Set<String> r){this.roles=r;} }