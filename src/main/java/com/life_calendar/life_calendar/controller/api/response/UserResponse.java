package com.life_calendar.life_calendar.controller.api.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDateTime birthday;
    private String imageUrl;

    public UserResponse(Long id, String firstname, String lastname, String email, LocalDateTime birthday) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.birthday = birthday;
    }
}
