package com.life_calendar.life_calendar.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(name="note")
@Entity(name = "note")
@NoArgsConstructor
public class Note {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String noteId;
    private String email;
    private String content = "";
    private LocalDateTime createdAt;
    private LocalDateTime lastEditedAt;
    private boolean isEdited = true;

    public Note(String noteId, String email, LocalDateTime createdAt, LocalDateTime lastEditedAt) {
        this.noteId = noteId;
        this.email = email;
        this.createdAt = createdAt;
        this.lastEditedAt = lastEditedAt;
    }
}
