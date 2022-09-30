package com.life_calendar.life_calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@Entity(name = "Users")
@Table(name="Users")
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
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
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private Boolean locked = false;
    private Boolean enabled = false;
    private String resetCode = null;

    public User(String username, String email, LocalDate birthday, String password ,UserRole userRole) {
        this.username = username;
        this.email = email;
        this.birthday = birthday;
        this.password = password;
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(UserRole.USER.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.locked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
