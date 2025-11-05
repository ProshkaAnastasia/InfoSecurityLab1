package com.security.api.controller;

import com.security.api.dto.PostRequest;
import com.security.api.entity.Post;
import com.security.api.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    private final PostService svc;
    
    public PostController(PostService s) {
        this.svc = s;
    }
    
    @PostMapping
    public ResponseEntity<Post> create(
            @Valid @RequestBody PostRequest r,
            Authentication auth) {
        return ResponseEntity.ok(svc.create(r, auth.getName()));
    }
    
    @GetMapping
    public ResponseEntity<List<Post>> all() {
        return ResponseEntity.ok(svc.list());
    }
}