package com.ncnmo.aspire.elearning.controller;

import com.ncnmo.aspire.elearning.dto.LessonDTO;
import com.ncnmo.aspire.elearning.service.LessonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private static final Logger logger = LoggerFactory.getLogger(LessonController.class);

    @Autowired
    private LessonService lessonService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonDTO> createLesson(@RequestBody LessonDTO lessonDTO) {
        try {
            LessonDTO createdLesson = lessonService.saveLesson(lessonDTO);
            return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LessonDTO> editLesson(@PathVariable Long id, @RequestBody LessonDTO lessonDTO) {
        try {
            LessonDTO updatedLesson = lessonService.updateLesson(id, lessonDTO);
            return new ResponseEntity<>(updatedLesson, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByCourse(@PathVariable Long courseId) {
        logger.info("Received request to fetch lessons for courseId: {}", courseId);
        try {
            List<LessonDTO> lessons = lessonService.findLessonsByCourseId(courseId);
            if (lessons.isEmpty()) {
                logger.info("No lessons found for courseId: {}", courseId);
            } else {
                logger.info("Found {} lessons for courseId: {}", lessons.size(), courseId);
            }
            return new ResponseEntity<>(lessons, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Course not found with ID: {}", courseId, e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable Long id) {
        try {
            LessonDTO lessonDTO = lessonService.findById(id);
            return new ResponseEntity<>(lessonDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<LessonDTO>> getAllLessons() {
        return new ResponseEntity<>(lessonService.findAllLessons(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{courseId}/{currentLessonId}/next")
    public ResponseEntity<LessonDTO> getNextLesson(@PathVariable Long courseId, @PathVariable Long currentLessonId) {
        LessonDTO nextLesson = lessonService.getNextLesson(courseId, currentLessonId);
        if (nextLesson != null) {
            return new ResponseEntity<>(nextLesson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // No next lesson
        }
    }

    @GetMapping("/{courseId}/{currentLessonId}/previous")
    public ResponseEntity<LessonDTO> getPreviousLesson(@PathVariable Long courseId, @PathVariable Long currentLessonId) {
        LessonDTO previousLesson = lessonService.getPreviousLesson(courseId, currentLessonId);
        if (previousLesson != null) {
            return new ResponseEntity<>(previousLesson, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // No previous lesson
        }
    }
}
