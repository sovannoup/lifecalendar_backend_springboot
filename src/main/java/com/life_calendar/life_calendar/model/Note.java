package com.life_calendar.life_calendar.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name="note")
@Entity(name = "note")
@NoArgsConstructor
public class Note {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String boxId;
    private String email;
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
