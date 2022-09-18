package com.life_calendar.life_calendar.service.Authentication;

import com.life_calendar.life_calendar.controller.api.request.GetSingleNoteRequest;
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
    public Response getSingleNote(GetSingleNoteRequest request, String email){
        User isUser = userRepo.findByEmail(email);
        if(isUser == null)
        {
            throw new ApiRequestException("Email not exist");
        }
        Note isNote = noteRepo.findByIdAndEmail(Long.parseLong(request.getId()), email);
        if(isNote == null)
        {
            Map<String, String> result = new HashMap<>();
            result.put("result", "Note not found");
            Response res = new Response(
                    200,
                    "success",
                    result,
                    LocalDateTime.now()
            );
            return res;
        }
        Map<String, String> result = new HashMap<>();
        result.put("content", isNote.getContent());
        result.put("lastEdited", isNote.getLastEditedAt().toString());
        Response res = new Response(
                200,
                "success",
                result,
                LocalDateTime.now()
        );
        return res;
    }

    @Async
    public Response updateNote(NoteRequest request, String email)
    {
        User isUserExisted = userRepo.findByEmail(email);
        if(isUserExisted == null)
        {
            throw new ApiRequestException("Token is invalid");
        }
        if(request.getContent() == null){
            throw new ApiRequestException("Content is required");
        }
        Note note = noteRepo.findByNoteDateAndEmail(request.getNoteDate(), email);
        if(note == null)
        {
            Note newNote = new Note(request.getBoxId(), email, request.getNoteDate(), request.getContent(), LocalDateTime.now());
            noteRepo.save(newNote);
        }else{
            noteRepo.updateNoteContent(request.getNoteDate(), request.getContent(), email, LocalDateTime.now());
        }

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
