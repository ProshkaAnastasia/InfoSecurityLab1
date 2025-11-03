package com.security.api.dto;
import jakarta.validation.constraints.*;
public class UserRegistrationRequest { @NotBlank private String username; @NotBlank private String password; @Email private String email;
  public String getUsername(){return username;} public void setUsername(String u){this.username=u;}
  public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
  public String getEmail(){return email;} public void setEmail(String e){this.email=e;} }