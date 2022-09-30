package com.life_calendar.life_calendar.controller.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class UserProfileRequest {

    @NotBlank(message = "Email should not be blank")
    @NotNull(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Username should not be blank")
    @NotNull(message = "Username is required")
    @Size(max = 30, message = "Username must not be more than 30 digit")
    private String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "require at least 1 lower cha, 1 upper cha, 1 number, and at least 8 digit")
    private String currentPassword;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "require at least 1 lower cha, 1 upper cha, 1 number, and at least 8 digit")
    private String newPassword;

    @NotNull(message = "Birthday is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
