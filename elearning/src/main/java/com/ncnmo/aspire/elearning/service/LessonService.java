package com.ncnmo.aspire.elearning.service;

import com.ncnmo.aspire.elearning.dto.LessonDTO;
import com.ncnmo.aspire.elearning.model.Course;
import com.ncnmo.aspire.elearning.model.Lesson;
import com.ncnmo.aspire.elearning.repository.CourseRepository;
import com.ncnmo.aspire.elearning.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    public LessonDTO saveLesson(LessonDTO lessonDTO) {
        Lesson lesson = convertToEntity(lessonDTO);

        // Fetch the course by ID and set it in the lesson
        Course course = courseRepository.findById(lessonDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        lesson.setCourse(course);

        Lesson savedLesson = lessonRepository.save(lesson);
        return convertToDTO(savedLesson);
    }

    public LessonDTO updateLesson(Long id, LessonDTO lessonDTO) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        lesson.setTitle(lessonDTO.getTitle());

        // Update text content only if it's provided
        if (lessonDTO.getTextContent() != null) {
            lesson.setTextContent(lessonDTO.getTextContent());
        }

        Lesson updatedLesson = lessonRepository.save(lesson);
        return convertToDTO(updatedLesson);
    }

    @Transactional(readOnly = true)
    public List<LessonDTO> findLessonsByCourseId(Long courseId) {
        // Ensure that the course exists
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }

        // Fetch lessons by course ID
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        return lessons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LessonDTO findById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return convertToDTO(lesson);
    }

    public List<LessonDTO> findAllLessons() {
        return lessonRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LessonDTO getNextLesson(Long courseId, Long currentLessonId) {
        List<Lesson> nextLessons = lessonRepository.findNextLesson(courseId, currentLessonId);
        if (!nextLessons.isEmpty()) {
            return convertToDTO(nextLessons.get(0));  // Return the first lesson in the result
        }
        return null;  // No next lesson found
    }

    @Transactional(readOnly = true)
    public LessonDTO getPreviousLesson(Long courseId, Long currentLessonId) {
        List<Lesson> previousLessons = lessonRepository.findPreviousLesson(courseId, currentLessonId);
        if (!previousLessons.isEmpty()) {
            return convertToDTO(previousLessons.get(0));  // Return the first (previous) lesson
        }
        return null;  // No previous lesson found
    }

    // Conversion methods
    Lesson convertToEntity(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        lesson.setId(lessonDTO.getId());
        lesson.setTitle(lessonDTO.getTitle());
        lesson.setTextContent(lessonDTO.getTextContent());

        return lesson;
    }

    LessonDTO convertToDTO(Lesson lesson) {
        LessonDTO lessonDTO = new LessonDTO();
        lessonDTO.setId(lesson.getId());
        lessonDTO.setTitle(lesson.getTitle());
        lessonDTO.setTextContent(lesson.getTextContent());
        lessonDTO.setCourseId(lesson.getCourse().getId());

        return lessonDTO;
    }
}
