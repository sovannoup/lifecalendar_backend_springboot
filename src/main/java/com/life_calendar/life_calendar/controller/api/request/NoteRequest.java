package com.life_calendar.life_calendar.controller.api.request;

import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NoteRequest {
    @NotBlank(message = "Id should not be blank")
    @NotNull(message = "Id is required")
    private String id;
    private String content;
    private Long email;
}
