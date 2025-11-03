package com.security.api.controller;
import com.security.api.repository.UserRepository;import org.springframework.http.ResponseEntity;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api")
public class DataController { private final UserRepository repo; public DataController(UserRepository r){this.repo=r;}
  @GetMapping("/data") public ResponseEntity<?> data(Authentication auth){Map<String,Object> res=new HashMap<>();res.put("message","Data retrieved successfully");res.put("requestedBy",auth.getName());res.put("users",repo.findAll());res.put("count",repo.count());return ResponseEntity.ok(res);} }