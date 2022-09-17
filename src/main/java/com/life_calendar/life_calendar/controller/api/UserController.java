package com.life_calendar.life_calendar.controller.api;

import com.life_calendar.life_calendar.controller.api.request.*;
import com.life_calendar.life_calendar.controller.api.response.Response;
import com.life_calendar.life_calendar.exception.ApiRequestException;
import com.life_calendar.life_calendar.model.User;
import com.life_calendar.life_calendar.service.Authentication.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "lifecalendar")
public class UserController {

    private final UserService userService;

    @PostMapping("signup")
    public ResponseEntity<Response> signup(@Valid @RequestBody SignupRequest request){
        return ResponseEntity.ok().body(userService.signup(request));
    }

    @GetMapping(path = "signup/confirm")
    public ResponseEntity<Response> confirm(@RequestParam("token") String token) {
        if(token == null){
            throw new ApiRequestException("Token is required");
        }
        return ResponseEntity.ok().body(userService.confirmToken(token));
    }

    @PostMapping("reset")
    public ResponseEntity<Response> reset(@Valid @RequestBody ResetRequest request){
        return ResponseEntity.ok().body(userService.reset(request));
    }

    @PostMapping("updateResetPassword")
    public ResponseEntity<Response> updatePassword(@Valid @RequestBody UpdatePasswordRequest request, @RequestParam("code") String code) throws IOException{
        if (code == null){
            throw new ApiRequestException("Code is required");
        }
        return ResponseEntity.ok().body(userService.updateResetPassword(request, code));
    }


    @PutMapping("updateProfileImage")
    public ResponseEntity<Response> updateProfileImage(@RequestParam("imageUrl") MultipartFile file) throws IOException {
        return ResponseEntity.ok().body(userService.updateProfileImage(file));
    }

    @PutMapping("updateProfile")
    public ResponseEntity<Response> updateUserProfile(@Valid @RequestBody UserProfileRequest request) throws IOException {
        return ResponseEntity.ok().body(userService.updateUserProfile(request));
    }

    @GetMapping(path = "gethomeinfo")
    public ResponseEntity<Response> getHomeDisplayInfo(@Parameter(ref = "columnId") String columnId) throws IOException, ParseException {
        return ResponseEntity.ok().body(userService.getHomeDisplay(columnId));
    }
}
