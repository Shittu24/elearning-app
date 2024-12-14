package com.ncnmo.aspire.elearning.repository;

import com.ncnmo.aspire.elearning.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourseId(Long courseId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.id = :courseId")
    long countByCourseId(Long courseId);

    List<Lesson> findByCourseIdOrderByIdAsc(Long courseId); // Fetch lessons in ascending order by ID

    @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.id > :lessonId ORDER BY l.id ASC")
    List<Lesson> findNextLesson(Long courseId, Long lessonId); // Return List instead of Optional

    @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.id < :lessonId ORDER BY l.id DESC")
    List<Lesson> findPreviousLesson(Long courseId, Long lessonId); // Return List instead of Optional

}


