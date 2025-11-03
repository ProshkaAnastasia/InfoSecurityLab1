package com.security.api.controller;
import com.security.api.dto.*;import com.security.api.service.UserService;import jakarta.validation.Valid;import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/auth")
public class AuthController { private final UserService svc; public AuthController(UserService s){this.svc=s;}
  @PostMapping("/register") public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest r){return ResponseEntity.ok(svc.register(r));}
  @PostMapping("/login") public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest r){return ResponseEntity.ok(svc.authenticate(r));}}