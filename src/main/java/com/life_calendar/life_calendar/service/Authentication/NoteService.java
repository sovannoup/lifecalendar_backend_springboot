package com.life_calendar.life_calendar.service.Authentication;

import com.life_calendar.life_calendar.controller.api.request.NoteRequest;
import com.life_calendar.life_calendar.controller.api.response.Response;
import com.life_calendar.life_calendar.exception.ApiRequestException;
import com.life_calendar.life_calendar.model.Note;
import com.life_calendar.life_calendar.model.User;
import com.life_calendar.life_calendar.repo.NoteRepo.NoteRepo;
import com.life_calendar.life_calendar.repo.UserRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Data
public class NoteService {
    private final UserRepo userRepo;
    private final NoteRepo noteRepo;

    @Async
    public Response getNote(NoteRequest noteRequest, String email){
//        User isUser = userRepo.findByEmail(email);
//        if(isUser == null)
//        {
//            throw new ApiRequestException("Email not exist");
//        }
//        log.info("ID id : {}", noteRequest.getNoteId());
//        Note isNote = noteRepo.findByNoteIdAndEmail(noteRequest.getNoteId(), email);
//        if(isNote == null)
//        {
//            log.info("note is not exist !");
//            Note note = new Note(noteRequest.getNoteId(), email, LocalDateTime.now(), LocalDateTime.now());
//            noteRepo.save(note);
//            Map<String, String> result = new HashMap<>();
//            result.put("content", note.getContent());
//            Response res = new Response(
//                    200,
//                    "success",
//                    result,
//                    LocalDateTime.now()
//            );
//            return res;
//        }
//        log.info("note is exist !");

        Map<String, String> result = new HashMap<>();
//        result.put("content", isNote.getContent());
//        result.put("lastEdited", isNote.getLastEditedAt().toString());
        Response res = new Response(
                200,
                "success",
                result,
                LocalDateTime.now()
        );
        return res;
    }

    @Async
    public Response updateNote(NoteRequest noteRequest, String email)
    {
        User isUserExisted = userRepo.findByEmail(email);
        if(isUserExisted == null)
        {
            throw new ApiRequestException("Email doesn't exist");
        }
        if(noteRequest.getContent() == null){
            throw new ApiRequestException("Content is required");
        }
        noteRepo.updateNoteContent(noteRequest.getColumnId(), noteRequest.getContent(), email, LocalDateTime.now());

        Map<String, String> result = new HashMap<>();

        Response res = new Response(
                200,
                "success",
                result,
                LocalDateTime.now()
        );
        return res;
    }
}
