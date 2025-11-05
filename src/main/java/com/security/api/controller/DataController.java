package com.security.api.controller;

import com.security.api.dto.UserPublicDTO;
import com.security.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DataController {

    private final UserRepository userRepository;

    public DataController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData(Authentication auth) {
        Map<String, Object> response = new HashMap<>();

        List<UserPublicDTO> users = userRepository.findAll()
                .stream()
                .map(user -> new UserPublicDTO(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());

        response.put("message", "Data retrieved successfully");
        response.put("requestedBy", auth.getName());
        response.put("users", users);
        response.put("count", users.size());

        return ResponseEntity.ok(response);
    }
}