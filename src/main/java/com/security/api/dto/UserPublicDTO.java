package com.security.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPublicDTO {

    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    public UserPublicDTO() {}

    public UserPublicDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserPublicDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
