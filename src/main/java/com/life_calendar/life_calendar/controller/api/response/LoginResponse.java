package com.life_calendar.life_calendar.controller.api.response;

import lombok.Data;

@Data
public class LoginResponse {
    private final String result;
    private final String token;
}
