package com.life_calendar.life_calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Table(name="homeCalendar")
@Entity(name = "calendar")
@Data
@NoArgsConstructor
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Email should not be blank")
    @NotNull(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email = "";
    @NotBlank(message = "Column ID should not be blank")
    @NotNull(message = "Column ID is required")
    private String boxId = "";
    @NotNull(message = "Start Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @NotNull(message = "End Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public Calendar(String email, String boxId, LocalDate startDate, LocalDate endDate) {
        this.email = email;
        this.boxId = boxId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
