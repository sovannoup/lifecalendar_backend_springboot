package com.life_calendar.life_calendar.model;

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
    private String columnId = "";
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public Calendar(String email, String columnId, LocalDate dateFrom, LocalDate dateTo) {
        this.email = email;
        this.columnId = columnId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }
}
