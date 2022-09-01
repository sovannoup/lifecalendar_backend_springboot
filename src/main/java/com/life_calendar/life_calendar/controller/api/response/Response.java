package com.life_calendar.life_calendar.controller.api.response;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Response {
    private final int status;
    private final String message;
    private final Object result;
    private final LocalDateTime date;
}
