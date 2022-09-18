package com.life_calendar.life_calendar.controller.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class NoteRequest {
    @NotBlank(message = "Id should not be blank")
    @NotNull(message = "Id is required")
    private String boxId;

    @NotNull(message = "Start date should not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate noteDate;

    private String content;
}
