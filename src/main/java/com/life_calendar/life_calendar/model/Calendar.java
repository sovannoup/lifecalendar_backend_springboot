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
    private String boxId = "";
    private LocalDate startDate;
    private LocalDate endDate;

    public Calendar(String email, String boxId, LocalDate startDate, LocalDate endDate) {
        this.email = email;
        this.boxId = boxId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
