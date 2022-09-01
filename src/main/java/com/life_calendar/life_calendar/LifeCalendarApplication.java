package com.life_calendar.life_calendar;

import com.life_calendar.life_calendar.controller.api.request.SignupRequest;
import com.life_calendar.life_calendar.model.User;
import com.life_calendar.life_calendar.model.UserRole;
import com.life_calendar.life_calendar.service.Authentication.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication

@OpenAPIDefinition(info = @Info(title = "Life Calendar", version = "1.0", description = "Life Calendar"))
@SecurityScheme(name = "lifecalendar", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class LifeCalendarApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifeCalendarApplication.class, args);
	}

//	CommandLineRunner run(UserService userService){
//		return args -> {
//			userService.signup(new SignupRequest("Hiro", "Sovan", "Noup", "noupsovan18@gmail.com", LocalDateTime.now(),"Sovan12345"));
//		};
//	}
}
