package com.life_calendar.life_calendar.repo.NoteRepo;
import com.life_calendar.life_calendar.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional
public interface NoteRepo extends JpaRepository<Note, Long> {
    Note findByNoteIdAndEmail(String id, String email);

    @Transactional
    @Modifying
    @Query("UPDATE note c " +
            "SET c.content = ?2, c.lastEditedAt = ?4 " +
            "WHERE c.noteId = ?1 and c.email = ?3")
    int updateNoteContent(String noteId, String content, String email, LocalDateTime lastEditedAt);
}
