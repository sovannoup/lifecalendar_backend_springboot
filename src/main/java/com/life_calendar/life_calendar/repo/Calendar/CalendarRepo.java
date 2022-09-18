package com.life_calendar.life_calendar.repo.Calendar;

import com.life_calendar.life_calendar.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Repository
@Transactional
public interface CalendarRepo extends JpaRepository<Calendar, Long> {
    Calendar findByBoxIdAndEmail(String boxId, String email);
}
