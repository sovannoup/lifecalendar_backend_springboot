package com.life_calendar.life_calendar.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Table(name="confirmtoken")
@Entity(name = "confirmtoken")
@NoArgsConstructor
public class ConfirmToken  {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Token should not be blank")
    @NotNull(message = "Token is required")
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name = "Users_Id"
    )
    private User user;

    public ConfirmToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, User user){
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }
}
