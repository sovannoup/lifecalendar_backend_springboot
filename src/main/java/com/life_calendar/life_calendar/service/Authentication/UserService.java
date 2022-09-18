package com.life_calendar.life_calendar.service.Authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.life_calendar.life_calendar.controller.api.request.*;
import com.life_calendar.life_calendar.controller.api.response.Response;
import com.life_calendar.life_calendar.controller.api.response.UserResponse;
import com.life_calendar.life_calendar.exception.ApiRequestException;
import com.life_calendar.life_calendar.model.*;
import com.life_calendar.life_calendar.model.Calendar;
import com.life_calendar.life_calendar.repo.Authentication.ConfirmTokenRepo;
import com.life_calendar.life_calendar.repo.Calendar.CalendarRepo;
import com.life_calendar.life_calendar.repo.NoteRepo.NoteRepo;
import com.life_calendar.life_calendar.repo.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
@AllArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    ServletContext context;
    @Autowired
    HttpServletRequest httpServletRequest;
    private final UserRepo userRepo;
    private final ConfirmTokenRepo confirmTokenRepo;
    private final CalendarRepo calendarRepo;
    private final NoteRepo noteRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmTokenService confirmTokenService;
    private final EmailSenderService emailSenderService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if(user == null){
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }else if(!user.isEnabled())
        {
            throw new ApiRequestException("not verified");
        }
        else {
            log.info("User {} found", email);
        }
        Collection<SimpleGrantedAuthority> authority = new ArrayList<>();
            authority.add(new SimpleGrantedAuthority(user.getUserRole().name()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authority);
    }

    public Response signup(SignupRequest request){
        User isUser = userRepo.findByEmail(request.getEmail());
        if(isUser != null && isUser.getEnabled())
        {
            throw new ApiRequestException("Email already exist");
        }
        String encodedPass = bCryptPasswordEncoder.encode(request.getPassword());
        String verifyToken = UUID.randomUUID().toString();
        if(isUser != null && !isUser.getEnabled())
        {
            userRepo.updateUser(request.getUsername(), encodedPass, request.getBirthday(), request.getEmail());
            confirmTokenRepo.updateToken(
                    verifyToken,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    isUser.getEmail()
            );
        }else
        {
            User user = new User(request.getUsername(),request.getEmail(), request.getBirthday(), encodedPass, UserRole.USER);
            log.info(user.toString());
            userRepo.save(user);

            ConfirmToken confirmToken = new ConfirmToken(
                    verifyToken,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    user.getEmail()
            );
            log.info("Verify Token is :{}",verifyToken);

            confirmTokenService.saveConfirmToken(confirmToken);
        }

//        String link = "http://localhost:8080/api/signup/confirm?token=" + token;
//        emailSenderService.send(request.getEmail(), buildEmail(request.getFirstname(), link));
        Map<String, String> result = new HashMap<>();
        result.put("verifyCode", verifyToken);
        return new Response(
                200,
                "Signup successfully",
                result,
                LocalDateTime.now()
        );
    }

    public Response reset(ResetRequest request){
        User isUserExisted = userRepo.findByEmail(request.getEmail());
        if(isUserExisted == null)
        {
            throw new ApiRequestException("Email doesn't exist");
        }
//        SecureRandom secureRandom = new SecureRandom();
//        int randomInt = secureRandom.nextInt(999999 - 100000) + 100000;
        String verifyToken = UUID.randomUUID().toString();

        Algorithm algorithm = Algorithm.HMAC256("yUl7speiRyENloYHUGJEFM0OzeBbcskjDB74A2cvZHqjpojeiSceNOARQcJmsev4".getBytes());
        userRepo.updateResetCode(request.getEmail(), verifyToken);
        String code = JWT.create()
                .withSubject(verifyToken)
                .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // 5 min
                .withIssuer(request.getEmail())
                .sign(algorithm);

//        emailSenderService.send(request.getEmail(), buildEmail(request.getFirstname(), code));

        Map<String, Object> result = new HashMap<>();
        result.put("verifyCode", code);
        return new Response(
                200,
                "Verify code already sent",
                result,
                LocalDateTime.now()
        );
    }


    public Response getBoxInfo(GetWeeklyNoteRequest request) {
        Map<String, Object> result = new HashMap<>();
        String email = getEmailFromToken();

        //        Find user
        User user = userRepo.findByEmail(email);
        if(user == null){
            throw new ApiRequestException("Token is invalid");
        }
        String boxId = user.getId() + "/" + request.getStartDate().toString();

        Calendar calendar = calendarRepo.findByBoxIdAndEmail(boxId, email);
        if(calendar == null) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            java.util.Calendar calen = java.util.Calendar.getInstance();
//            Date bd = Date.from(user.getBirthday().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
//            calen.setTime(bd);
//            calen.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
//            calen.add(java.util.Calendar.WEEK_OF_YEAR, Integer.parseInt(boxId) + 1);
//            LocalDate d_from = LocalDate.parse(formatter.format(calen.getTime()));
//            List<LocalDate> dailyDate = new ArrayList<>();
//            dailyDate.add(d_from);
//            for (int i = 0; i < 6; i++) {
//                calen.add(java.util.Calendar.DAY_OF_WEEK, 1);
//                LocalDate d = LocalDate.parse(formatter.format(calen.getTime()));
//                dailyDate.add(d);
//            }
//            calen.add(java.util.Calendar.DAY_OF_WEEK, 6);
//            calen.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
//            LocalDate d_to = LocalDate.parse(formatter.format(calen.getTime()));
            calendar = new Calendar(email, boxId, request.getStartDate(), request.getEndDate());

            calendarRepo.save(calendar);
            List<Note> notes = new ArrayList<>();
//            notes = noteRepo.findByBoxIdAndEmail(boxId, email);
//            for (int i = 0; i < 7; i++) {
//                Note temp = new Note(calendar.getBoxId(), email,dailyDate.get(i), "", LocalDateTime.now());
//                notes.add(temp);
//            }
            result.put("notes", notes);
        }else {
            List<Note> notes =  noteRepo.findByBoxIdAndEmail(calendar.getBoxId(), email);
            result.put("notes", notes);

            //date cal
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            java.util.Calendar calen = java.util.Calendar.getInstance();
//            Date bd = Date.from(user.getBirthday().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
//            calen.setTime(bd);
//            calen.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
//            calen.add(java.util.Calendar.WEEK_OF_YEAR, Integer.parseInt(request.getBoxId()) + 1);
//            LocalDate d_from = LocalDate.parse(formatter.format(calen.getTime()));
//            List<LocalDate> dailyDate = new ArrayList<>();
//            dailyDate.add(d_from);
//            for (int i = 0; i < 6; i++) {
//                calen.add(java.util.Calendar.DAY_OF_WEEK, 1);
//                LocalDate d = LocalDate.parse(formatter.format(calen.getTime()));
//                dailyDate.add(d);
//            }
//            if (!notes.isEmpty()){
//                result.put("notes", notes);
//            }else{
//                notes = new ArrayList<>();
//                for (int i = 0; i < 7; i++) {
//                    Note temp = new Note(calendar.getBoxId(), email, dailyDate.get(i), "", LocalDateTime.now());
//                    notes.add(temp);
//                }
//                result.put("notes", notes);
//            }
        }

        return new Response(
                200,
                "user information",
                result,
                LocalDateTime.now()
        );


//Backup
//        Map<String, Object> result = new HashMap<>();
//
//        //        Find user
//        User user = userRepo.findByEmail(request.getEmail());
//        if(user == null){
//            throw new ApiRequestException("Token is invalid");
//        }
//
//        Calendar calendar = calendarRepo.findByBoxIdAndEmail(request.getBoxId(), request.getEmail());
//        if(calendar == null) {
//
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            java.util.Calendar calen = java.util.Calendar.getInstance();
//            Date bd = Date.from(user.getBirthday().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
//            calen.setTime(bd);
//            calen.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
//            calen.add(java.util.Calendar.WEEK_OF_YEAR, Integer.parseInt(request.getBoxId()) + 1);
//            LocalDate d_from = LocalDate.parse(formatter.format(calen.getTime()));
//            List<LocalDate> dailyDate = new ArrayList<>();
//            dailyDate.add(d_from);
//            for (int i = 0; i < 6; i++) {
//                calen.add(java.util.Calendar.DAY_OF_WEEK, 1);
//                LocalDate d = LocalDate.parse(formatter.format(calen.getTime()));
//                dailyDate.add(d);
//            }
//            calen.add(java.util.Calendar.DAY_OF_WEEK, 6);
//            calen.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
//            LocalDate d_to = LocalDate.parse(formatter.format(calen.getTime()));
//            calendar = new Calendar(request.getEmail(), request.getBoxId(), d_from, d_to);
//
//            calendarRepo.save(calendar);
//            List<Note> notes = new ArrayList<>();
//            for (int i = 0; i < 7; i++) {
//                Note temp = new Note(calendar.getBoxId(), request.getEmail(),dailyDate.get(i), "", LocalDateTime.now());
//                notes.add(temp);
//            }
//            result.put("notes", notes);
//        }else {
//            List<Note> notes =  noteRepo.findByBoxIdAndEmail(calendar.getBoxId(), request.getEmail());
//
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            java.util.Calendar calen = java.util.Calendar.getInstance();
//            Date bd = Date.from(user.getBirthday().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
//            calen.setTime(bd);
//            calen.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
//            calen.add(java.util.Calendar.WEEK_OF_YEAR, Integer.parseInt(request.getBoxId()) + 1);
//            LocalDate d_from = LocalDate.parse(formatter.format(calen.getTime()));
//            List<LocalDate> dailyDate = new ArrayList<>();
//            dailyDate.add(d_from);
//            for (int i = 0; i < 6; i++) {
//                calen.add(java.util.Calendar.DAY_OF_WEEK, 1);
//                LocalDate d = LocalDate.parse(formatter.format(calen.getTime()));
//                dailyDate.add(d);
//            }
//
//            if (!notes.isEmpty()){
//                result.put("notes", notes);
//            }else{
//                notes = new ArrayList<>();
//                for (int i = 0; i < 7; i++) {
//                    Note temp = new Note(calendar.getBoxId(), request.getEmail(), dailyDate.get(i), "", LocalDateTime.now());
//                    notes.add(temp);
//                }
//                result.put("notes", notes);
//            }
//        }
//
//        return new Response(
//                200,
//                "user information",
//                result,
//                LocalDateTime.now()
//        );
    }


    public Response getHomePage(GetWeeklyNoteRequest request) {

        Map<String, Object> result = new HashMap<>();
        String email = getEmailFromToken();

        //        Find user
        User user = userRepo.findByEmail(email);
        if(user == null){
            throw new ApiRequestException("Token is invalid");
        }

        UserResponse userInfo = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getBirthday());
        result.put("userInfo", userInfo);


////        Get week note
//        java.util.Calendar cal = java.util.Calendar.getInstance();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        cal.setTime(new Date());
//        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
//
////        Date From Mon
//        LocalDate d_from = LocalDate.parse(formatter.format(cal.getTime()));
//
//        //Each day of 1 week
//        List<LocalDate> dailyDate = new ArrayList<>();
//        dailyDate.add(d_from);
//        for (int i = 0; i < 6; i++) {
//            cal.add(java.util.Calendar.DAY_OF_WEEK, 1);
//            LocalDate d = LocalDate.parse(formatter.format(cal.getTime()));
//            dailyDate.add(d);
//        }
//
//
//        cal.add(java.util.Calendar.DAY_OF_WEEK, 6);
//        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
//        LocalDate d_to = LocalDate.parse(formatter.format(cal.getTime()));

//        Date To Sun

        String boxId = user.getId() + "/" + request.getStartDate().toString();
        Calendar calendar = calendarRepo.findByBoxIdAndEmail(boxId, email);
        if(calendar == null) {
            // weeks from birthday
//            DateTime d1 = new DateTime(DateTime.parse(user.getBirthday().toString()));
//            DateTime d2 = new DateTime();
//
//            String weeksIdOrColumnId = String.valueOf(Weeks.weeksBetween(d1, d2).getWeeks());
            calendar = new Calendar(email, boxId, request.getStartDate(), request.getEndDate());
            calendarRepo.save(calendar);
            List<Note> notes = new ArrayList<>();
//            for (int i = 0; i < 7; i++) {
//                Note temp = new Note(calendar.getBoxId(), email ,dailyDate.get(i), "", LocalDateTime.now());
//                notes.add(temp);
//            }
            result.put("notes", notes);
        }else {
            List<Note> notes =  noteRepo.findByBoxIdAndEmail(calendar.getBoxId(), email);
            result.put("notes", notes);
//            if (!notes.isEmpty()){
//                result.put("notes", notes);
//            }else{
//                notes = new ArrayList<>();
//                for (int i = 0; i < 7; i++) {
//                    Note temp = new Note(calendar.getBoxId(), email ,dailyDate.get(i), "", LocalDateTime.now());
//                    notes.add(temp);
//                }
//                result.put("notes", notes);
//            }
        }

        return new Response(
                200,
                "user information",
                result,
                LocalDateTime.now()
        );
    }

    @Transactional
    public Response updateUserProfile(UserProfileRequest request){
        if(request.getCurrentPassword() != null && request.getNewPassword() != null){
            User user = userRepo.findByEmail(request.getEmail());
            if(!bCryptPasswordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            {
                throw new ApiRequestException("Invalid current password");
            }
            String newEncode = bCryptPasswordEncoder.encode(request.getNewPassword());
            userRepo.updateUserProfile(request.getUsername(), newEncode , LocalDateTime.parse(request.getBirthday()), request.getEmail());
            log.info("Updating info with password input");
        }else{
            boolean isUser = userRepo.existsByEmail(request.getEmail());
            if(!isUser)
            {
                throw new ApiRequestException("Couldn't find user with that Email");
            }
            userRepo.updateProfileWithoutPassword(request.getUsername(), LocalDateTime.parse(request.getBirthday()), request.getEmail());
            log.info("Updating info without password input");
        }

        return new Response(
                200,
                "user information updated",
                null,
                LocalDateTime.now()
        );
    }

    @Transactional
    public Response updateResetPassword(UpdatePasswordRequest request, String code){
        if(code == null)
        {
            throw new ApiRequestException("Code is required");
        }

        if(request.getPassword() == null){
            throw new ApiRequestException("Invalid password");
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256("yUl7speiRyENloYHUGJEFM0OzeBbcskjDB74A2cvZHqjpojeiSceNOARQcJmsev4".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(code);
            String resetCode = decodedJWT.getSubject();
            String email = decodedJWT.getIssuer();

            User user = userRepo.findByEmailAndResetCode(email, resetCode);
            if(user == null)
            {
                throw new ApiRequestException("Invalid code");
            }else {
                String encodedPass = bCryptPasswordEncoder.encode(request.getPassword());
                userRepo.updatePassword(email, encodedPass);
                userRepo.updateResetCode(email, "");
            }
        }catch (TokenExpiredException e){
            throw new ApiRequestException(e.getMessage());
        }

        return new Response(
                200,
                "Password already updated",
                null,
                LocalDateTime.now()
        );
    }

    @Transactional
    public Response updateProfileImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new ApiRequestException("Please load a file");
        }

        byte[] bytes = file.getBytes();
        Path path = Paths.get(context.getRealPath("uploads") + file.getOriginalFilename());
        Files.write(path, bytes);

        return new Response(
                200,
                "Profile picture already uploaded",
                null,
                LocalDateTime.now()
        );
    }

    @Transactional
    public Response confirmToken(String token){
        ConfirmToken confirmToken = confirmTokenService.getToken(token).orElseThrow(()-> new ApiRequestException("Token not found"));
        if (confirmToken.getConfirmedAt() != null) {
            throw new ApiRequestException("Email is already confirmed");
        }

        LocalDateTime expiredAt = confirmToken.getExpiresAt();
        if(expiredAt.isBefore(LocalDateTime.now())){
            throw new ApiRequestException("Confirm Token expired");
        }

        confirmTokenService.setConfirmedAt(token);
        userRepo.enableUser(confirmToken.getEmail());

        return new Response(
                200,
                "Account is verified",
                null,
                LocalDateTime.now()
        );
    }

    public String getEmailFromToken(){
        String headerAuth = httpServletRequest.getHeader(AUTHORIZATION);
        String token =  headerAuth.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("yUl7speiRyENloYHUGJEFM0OzeBbcskjDB74A2cvZHqjpojeiSceNOARQcJmsev4".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }
    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
