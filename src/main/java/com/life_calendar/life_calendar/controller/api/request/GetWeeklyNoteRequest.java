package com.life_calendar.life_calendar.controller.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class GetWeeklyNoteRequest {

    @NotNull(message = "Start date should not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @NotNull(message = "End date should not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;
}
