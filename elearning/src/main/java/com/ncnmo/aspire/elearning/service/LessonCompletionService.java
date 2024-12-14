package com.ncnmo.aspire.elearning.service;

import com.ncnmo.aspire.elearning.dto.LessonCompletionDTO;
import com.ncnmo.aspire.elearning.dto.LessonDTO;
import com.ncnmo.aspire.elearning.model.Lesson;
import com.ncnmo.aspire.elearning.model.LessonCompletion;
import com.ncnmo.aspire.elearning.model.User;
import com.ncnmo.aspire.elearning.repository.LessonCompletionRepository;
import com.ncnmo.aspire.elearning.repository.LessonRepository;
import com.ncnmo.aspire.elearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LessonCompletionService {
    @Autowired
    private LessonCompletionRepository lessonCompletionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonService lessonService;

    @Transactional
    public LessonCompletionDTO saveLessonCompletion(LessonCompletionDTO lessonCompletionDTO) {
        // Check if a completion already exists for this user and lesson
        Optional<LessonCompletion> existingCompletion = lessonCompletionRepository.findByUserIdAndLessonId(
                lessonCompletionDTO.getUserId(), lessonCompletionDTO.getLessonId());

        if (existingCompletion.isPresent()) {
            throw new RuntimeException("Lesson completion already exists for this lesson and user.");
        }

        // Convert DTO to entity
        LessonCompletion lessonCompletion = convertToEntity(lessonCompletionDTO);

        // Save entity
        LessonCompletion savedLessonCompletion = lessonCompletionRepository.save(lessonCompletion);

        // Convert back to DTO and return
        return convertToDTO(savedLessonCompletion);
    }

    @Transactional(readOnly = true)
    public List<LessonCompletionDTO> findLessonCompletionsByUserId(Long userId) {
        List<LessonCompletion> lessonCompletions = lessonCompletionRepository.findLessonCompletionsByUserId(userId);

        return lessonCompletions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LessonCompletionDTO> findLessonCompletionsByLessonId(Long lessonId) {
        return lessonCompletionRepository.findByLessonId(lessonId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteLessonCompletion(Long id) {
        lessonCompletionRepository.deleteById(id);
    }

    public boolean isCourseCompletedByUser(Long userId, Long courseId) {
        // Get the total number of lessons for the course
        long totalLessons = lessonRepository.countByCourseId(courseId);

        // Get the number of completed lessons for this course by the user
        List<LessonCompletion> completedLessons = lessonCompletionRepository.findCompletedLessonsByUserAndCourse(userId, courseId);

        // Remove duplicates from completed lessons list
        Set<Long> completedLessonIds = completedLessons.stream()
                .map(lessonCompletion -> lessonCompletion.getLesson().getId())
                .collect(Collectors.toSet());

        // Compare the number of unique completed lessons with the total lessons
        return totalLessons == completedLessonIds.size();
    }

    public String getIncompleteLessonsMessage(Long userId, Long courseId) {
        // Fetch the total lessons and completed lessons
        long totalLessons = lessonRepository.countByCourseId(courseId);
        List<LessonCompletion> completedLessons = lessonCompletionRepository.findCompletedLessonsByUserAndCourse(userId, courseId);

        Set<Long> completedLessonIds = completedLessons.stream()
                .map(lessonCompletion -> lessonCompletion.getLesson().getId())
                .collect(Collectors.toSet());

        // Fetch incomplete lessons
        List<LessonDTO> incompleteLessons = lessonService.findLessonsByCourseId(courseId).stream()
                .filter(lesson -> !completedLessonIds.contains(lesson.getId()))
                .collect(Collectors.toList());

        // Build message
        if (incompleteLessons.isEmpty()) {
            return "User with id " + userId + " has completed all lessons in course with id " + courseId + ".";
        } else {
            String incompleteLessonsNames = incompleteLessons.stream()
                    .map(LessonDTO::getTitle)
                    .collect(Collectors.joining(", "));
            return "User with id " + userId + " has not completed the course with id " + courseId + ".\nIncomplete Lessons: " + incompleteLessonsNames;
        }
    }


    // Convert Entity to DTO
    private LessonCompletionDTO convertToDTO(LessonCompletion lessonCompletion) {
        LessonCompletionDTO dto = new LessonCompletionDTO();
        dto.setId(lessonCompletion.getId());
        dto.setUserId(lessonCompletion.getUser().getId());
        dto.setLessonId(lessonCompletion.getLesson().getId());
        dto.setCompleted(lessonCompletion.isCompleted());
        return dto;
    }

    // Convert DTO to Entity
    private LessonCompletion convertToEntity(LessonCompletionDTO lessonCompletionDTO) {
        LessonCompletion lessonCompletion = new LessonCompletion();

        // Get User entity by userId
        User user = userRepository.findById(lessonCompletionDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        lessonCompletion.setUser(user);

        // Get Lesson entity by lessonId
        Lesson lesson = lessonRepository.findById(lessonCompletionDTO.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        lessonCompletion.setLesson(lesson);

        lessonCompletion.setCompleted(lessonCompletionDTO.isCompleted());
        return lessonCompletion;
    }
}
