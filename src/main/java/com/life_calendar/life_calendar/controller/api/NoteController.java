package com.life_calendar.life_calendar.controller.api;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.life_calendar.life_calendar.controller.api.request.NoteRequest;
import com.life_calendar.life_calendar.controller.api.request.UpdatePasswordRequest;
import com.life_calendar.life_calendar.controller.api.response.Response;
import com.life_calendar.life_calendar.service.Authentication.NoteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/note/")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "note")
public class NoteController {

    @Autowired
    HttpServletRequest userRequest;
    private final NoteService noteService;

    public String getEmailHeader(){
        String auth = userRequest.getHeader("Authorization");
        String token = auth.substring("Bearer ".length());

        Algorithm algorithm = Algorithm.HMAC256("yUl7speiRyENloYHUGJEFM0OzeBbcskjDB74A2cvZHqjpojeiSceNOARQcJmsev4".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }

    @GetMapping("get")
    public ResponseEntity<Response> getNote(@Valid NoteRequest request){
        String email = getEmailHeader();
        return ResponseEntity.ok().body(noteService.getNote(request, email));
    }
    @PostMapping("update")
    public ResponseEntity<Response> updateNote(@Valid NoteRequest request){
        String email = getEmailHeader();
        return ResponseEntity.ok().body(noteService.updateNote(request, email));
    }
}
