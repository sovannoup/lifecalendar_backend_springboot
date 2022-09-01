package com.life_calendar.life_calendar.repo;

import com.life_calendar.life_calendar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    @Transactional
    @Modifying
    @Query("UPDATE Users a " +
            "SET a.password = ?2 WHERE a.email = ?1")
    int updatePassword(String email, String password);

    @Transactional
    @Modifying
    @Query("UPDATE Users a " +
            "SET a.isVerified = TRUE WHERE a.email = ?1")
    int enableUser(String email);
}
