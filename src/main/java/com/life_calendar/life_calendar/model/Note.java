package com.life_calendar.life_calendar.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name="note")
@Entity(name = "note")
@NoArgsConstructor
public class Note {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull(message = "BoxId is required")
    private String boxId;
    @Email
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Note Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate noteDate;
    private String content = "";
    private LocalDateTime lastEditedAt;

    public Note(String boxId, String email, LocalDate noteDate, String content, LocalDateTime lastEditedAt) {
        this.boxId = boxId;
        this.email = email;
        this.noteDate = noteDate;
        this.content = content;
        this.lastEditedAt = lastEditedAt;
    }
}
