package com.ncnmo.aspire.elearning.controller;

import com.ncnmo.aspire.elearning.dto.CourseDTO;
import com.ncnmo.aspire.elearning.service.CourseService;
import com.ncnmo.aspire.elearning.service.LessonCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonCompletionService lessonCompletionService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(@RequestParam("title") String title,
                                                  @RequestParam("description") String description,
                                                  @RequestParam("image") MultipartFile image) {
        try {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setTitle(title);
            courseDTO.setDescription(description);

            CourseDTO createdCourse = courseService.saveCourse(courseDTO, image);
            return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> editCourse(@PathVariable Long id,
                                                @RequestParam("title") String title,
                                                @RequestParam("description") String description,
                                                @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setTitle(title);
            courseDTO.setDescription(description);

            CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO, image);
            return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.findAll();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.findById(id);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<CourseDTO> getCourseByLessonId(@PathVariable Long lessonId) {
        try {
            CourseDTO course = courseService.findCourseByLessonId(lessonId);
            return new ResponseEntity<>(course, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/courseCompleted/{userId}/{courseId}")
    public ResponseEntity<String> isCourseCompletedByUser(@PathVariable Long userId, @PathVariable Long courseId) {
        // Delegate course completion check to the service layer
        boolean isCompleted = lessonCompletionService.isCourseCompletedByUser(userId, courseId);

        // Provide feedback to the user based on completion status
        if (isCompleted) {
            return new ResponseEntity<>("User with id " + userId + " has completed the course with id " + courseId, HttpStatus.OK);
        } else {
            // Retrieve incomplete lessons via the service
            String incompleteLessonsMessage = lessonCompletionService.getIncompleteLessonsMessage(userId, courseId);
            return new ResponseEntity<>(incompleteLessonsMessage, HttpStatus.OK);
        }
    }
}
