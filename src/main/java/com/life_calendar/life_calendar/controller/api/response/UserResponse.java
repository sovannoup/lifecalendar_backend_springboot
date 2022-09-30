package com.life_calendar.life_calendar.controller.api.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDate birthday;
    private String imageUrl;

    public UserResponse(Long id, String username, String email, LocalDate birthday) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.birthday = birthday;
    }
}
