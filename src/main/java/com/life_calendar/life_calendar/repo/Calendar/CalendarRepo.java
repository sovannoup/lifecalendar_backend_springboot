package com.life_calendar.life_calendar.repo.Calendar;

import com.life_calendar.life_calendar.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@Transactional
public interface CalendarRepo extends JpaRepository<Calendar, Long> {
    Calendar findByDateFromAndEmail(LocalDate d_from, String Email);
    Calendar findByColumnIdAndEmail(String columnId, String email);
}
