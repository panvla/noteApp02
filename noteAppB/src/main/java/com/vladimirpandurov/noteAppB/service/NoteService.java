package com.vladimirpandurov.noteAppB.service;

import com.vladimirpandurov.noteAppB.domain.HttpResponse;
import com.vladimirpandurov.noteAppB.domain.Note;
import com.vladimirpandurov.noteAppB.enumeration.Level;
import com.vladimirpandurov.noteAppB.exception.NoteNotFoundException;
import com.vladimirpandurov.noteAppB.repository.NoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.vladimirpandurov.noteAppB.util.DateUtil.dateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;

    public HttpResponse<Note> getNotes() {
        log.info("Fetching all the notes from the database");
        return HttpResponse.<Note>builder()
                .notes(this.noteRepository.findAll())
                .message(this.noteRepository.count() > 0 ? this.noteRepository.count() + " notes retrieved" : "No notes to display")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

    public HttpResponse<Note> filterNotes(Level level) {
        List<Note> notes = this.noteRepository.findByLevel(level);
        log.info("Filtering notes by level {}", level);
        return HttpResponse.<Note>builder()
                .notes(notes)
                .message(notes.size() + " notes are of " + level + " importance")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

    public HttpResponse<Note> saveNote(Note note) {
        log.info("Savin new note to the database");
        note.setCreatedAt(LocalDateTime.now());
        return HttpResponse.<Note>builder()
                .notes(Collections.singleton(this.noteRepository.save(note)))
                .message("Note created successfully")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

    public HttpResponse<Note> updateNote(Note note) throws NoteNotFoundException {
        log.info("Updating note to the database");
        Optional<Note> optionalNote = Optional.ofNullable(this.noteRepository.findById(note.getId())).orElseThrow(() -> new NoteNotFoundException("The note was not found on the server"));
        Note updateNote = optionalNote.get();
        updateNote.setId(note.getId());
        updateNote.setTitle(note.getTitle());
        updateNote.setDescription(note.getDescription());
        updateNote.setLevel(note.getLevel());
        this.noteRepository.save(updateNote);
        return HttpResponse.<Note>builder()
                .notes(Collections.singleton(updateNote))
                .message("Note updated successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

    public HttpResponse<Note> deleteNote(Long id) throws NoteNotFoundException {
        log.info("Deleting note from the database by id {}", id);
        Optional<Note> optionalNote = Optional.ofNullable(this.noteRepository.findById(id).orElseThrow(()-> new NoteNotFoundException("The note was not found on the server")));
        optionalNote.ifPresent(this.noteRepository::delete);
        return HttpResponse.<Note>builder()
                .notes(Collections.singleton(optionalNote.get()))
                .message("Note deleted successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .timeStamp(LocalDateTime.now().format(dateTimeFormatter()))
                .build();
    }

}
