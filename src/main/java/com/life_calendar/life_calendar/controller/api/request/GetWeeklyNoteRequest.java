package com.life_calendar.life_calendar.controller.api.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GetWeeklyNoteRequest {

    @NotBlank(message = "Id should not be blank")
    @NotNull(message = "Id is required")
    private String columnId;

    @NotBlank(message = "Email should not be blank")
    @NotNull(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;
}
