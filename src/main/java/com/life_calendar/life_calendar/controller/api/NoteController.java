package com.life_calendar.life_calendar.controller.api;


import com.life_calendar.life_calendar.controller.api.request.UpdatePasswordRequest;
import com.life_calendar.life_calendar.controller.api.response.Response;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/note/")
@RequiredArgsConstructor
@Slf4j
public class NoteController {
    @PostMapping("updatePassword")
    public ResponseEntity<Response> updatePassword(@Valid UpdatePasswordRequest request){
//        return ResponseEntity.ok().body(userService.updatePassword(request));
    }
}
