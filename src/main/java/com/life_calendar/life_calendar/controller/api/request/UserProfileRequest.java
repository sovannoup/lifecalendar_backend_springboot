package com.life_calendar.life_calendar.controller.api.request;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class UserProfileRequest {

    @NotBlank(message = "Email should not be blank")
    @NotNull(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Firstname should not be blank")
    @NotNull(message = "Firstname is required")
    @Size(max = 30, message = "Firstname must not be more than 30 digit")
    private String firstname;

    @NotBlank(message = "Lastname should not be blank")
    @NotNull(message = "Lastname is required")
    @Size(max = 30, message = "Lastname must not be more than 30 digit")
    private String lastname;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "require at least 1 lower cha, 1 upper cha, 1 number, and at least 8 digit")
    private String currentPassword;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "require at least 1 lower cha, 1 upper cha, 1 number, and at least 8 digit")
    private String newPassword;

    @NotNull(message = "Birthday is required")
    private  CharSequence birthday;
}
