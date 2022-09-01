package com.life_calendar.life_calendar.exception;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiException {
    private final int status;
    private final Object message;
    private final LocalDateTime date;
}
