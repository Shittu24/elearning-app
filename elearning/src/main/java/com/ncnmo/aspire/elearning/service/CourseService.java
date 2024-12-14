package com.ncnmo.aspire.elearning.service;

import com.ncnmo.aspire.elearning.dto.CourseDTO;
import com.ncnmo.aspire.elearning.dto.LessonDTO;
import com.ncnmo.aspire.elearning.model.Course;
import com.ncnmo.aspire.elearning.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LessonService lessonService;

    @Transactional
    public CourseDTO saveCourse(CourseDTO courseDTO, MultipartFile image) throws IOException {
        Course course = convertToEntity(courseDTO);

        if (image != null && !image.isEmpty()) {
            // Use the "course" folder when saving course images
            String fileName = fileStorageService.storeFile(image, "course");
            course.setImagePath(fileName);  // Store only the image file name
        }

        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO, MultipartFile image) throws IOException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());

        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.storeFile(image, "course");
            course.setImagePath(fileName);  // Update the file name
        }

        Course updatedCourse = courseRepository.save(course);
        return convertToDTO(updatedCourse);
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> findAll() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDTO findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        // Ensure LOB fields are accessed within the transaction if needed
        course.getLessons().forEach(lesson -> {
            String textContent = lesson.getTextContent();
            //String videoUrl = lesson.getVideoUrl();
        });
        return convertToDTO(course);
    }

    public void deleteCourse(Long id) throws IOException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (course.getImagePath() != null) {
            fileStorageService.deleteFile(course.getImagePath(), "course");
        }

        courseRepository.deleteById(id);
    }

    public CourseDTO findCourseByLessonId(Long lessonId) {
        Course course = courseRepository.findByLessonId(lessonId);
        if (course == null) {
            throw new RuntimeException("Course not found for lessonId: " + lessonId);
        }
        return convertToDTO(course);
    }

    CourseDTO convertToDTO(Course course) {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setTitle(course.getTitle());
        courseDTO.setDescription(course.getDescription());

        // Ensure the imagePath does not start with an extra slash
        String imagePath = course.getImagePath().startsWith("/")
                ? course.getImagePath().substring(1)
                : course.getImagePath();
        courseDTO.setImagePath("/api/files/course/" + imagePath);

        List<LessonDTO> lessonDTOs = course.getLessons() != null ? course.getLessons().stream()
                .map(lesson -> lessonService.convertToDTO(lesson))
                .collect(Collectors.toList()) : new ArrayList<>();

        courseDTO.setLessons(lessonDTOs);

        return courseDTO;
    }


    Course convertToEntity(CourseDTO courseDTO) {
        Course course = new Course();
        course.setId(courseDTO.getId());
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        return course;
    }
}