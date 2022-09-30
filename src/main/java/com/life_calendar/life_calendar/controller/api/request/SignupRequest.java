package com.life_calendar.life_calendar.controller.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SignupRequest {

    @NotBlank(message = "Username should not be blank")
    @NotNull(message = "Username is required")
    @Size(max = 30, message = "Username must not be more than 30 digit")
    private String username;

    @NotBlank(message = "Email should not be blank")
    @NotNull(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @Past
    @NotNull(message = "Birthday is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @NotBlank(message = "Password should not be blank")
    @NotNull(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "require at least 1 lower cha, 1 upper cha, 1 number, and at least 8 digit")
    private String password;
}
