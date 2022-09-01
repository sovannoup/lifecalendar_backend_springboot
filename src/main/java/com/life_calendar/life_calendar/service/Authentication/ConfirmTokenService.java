package com.life_calendar.life_calendar.service.Authentication;

import com.life_calendar.life_calendar.model.ConfirmToken;
import com.life_calendar.life_calendar.repo.Authentication.ConfirmTokenRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmTokenService {
    private final ConfirmTokenRepo confirmTokenRepo;

    public void saveConfirmToken(ConfirmToken confirmToken){
        confirmTokenRepo.save(confirmToken);
    }

    Optional<ConfirmToken> getToken(String token){
        return confirmTokenRepo.findByToken(token);
    }

    public int setConfirmedAt(String token){
        return confirmTokenRepo.updateConfirmedAt(token, LocalDateTime.now());
    }
}
