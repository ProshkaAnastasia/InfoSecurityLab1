package com.security.api.dto;
public class AuthResponse { private String token; private String message; private String username;
  public AuthResponse(String t,String m,String u){this.token=t;this.message=m;this.username=u;} public String getToken(){return token;} public String getMessage(){return message;} public String getUsername(){return username;} }