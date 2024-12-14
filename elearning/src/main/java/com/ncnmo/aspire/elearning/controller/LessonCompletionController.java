package com.ncnmo.aspire.elearning.controller;

import com.ncnmo.aspire.elearning.dto.LessonCompletionDTO;
import com.ncnmo.aspire.elearning.service.LessonCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessonCompletions")
public class LessonCompletionController {

    @Autowired
    private LessonCompletionService lessonCompletionService;

    @PostMapping("/create")
    public ResponseEntity<?> createLessonCompletion(@RequestBody LessonCompletionDTO lessonCompletionDTO) {
        try {
            LessonCompletionDTO createdCompletion = lessonCompletionService.saveLessonCompletion(lessonCompletionDTO);
            return new ResponseEntity<>(createdCompletion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Handle duplicate completion or any other runtime exception
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LessonCompletionDTO>> getLessonCompletionsByUser(@PathVariable Long userId) {
        List<LessonCompletionDTO> completions = lessonCompletionService.findLessonCompletionsByUserId(userId);
        return new ResponseEntity<>(completions, HttpStatus.OK);
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<LessonCompletionDTO>> getLessonCompletionsByLesson(@PathVariable Long lessonId) {
        List<LessonCompletionDTO> completions = lessonCompletionService.findLessonCompletionsByLessonId(lessonId);
        return new ResponseEntity<>(completions, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLessonCompletion(@PathVariable Long id) {
        lessonCompletionService.deleteLessonCompletion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
