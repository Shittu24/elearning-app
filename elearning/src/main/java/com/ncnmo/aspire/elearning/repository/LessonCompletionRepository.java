package com.ncnmo.aspire.elearning.repository;

import com.ncnmo.aspire.elearning.model.LessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Long> {

    // Find completion by user and lesson
    Optional<LessonCompletion> findByUserIdAndLessonId(Long userId, Long lessonId);

    // Find all lesson completions by user
    @Query("SELECT lc FROM LessonCompletion lc WHERE lc.user.id = :userId")
    List<LessonCompletion> findLessonCompletionsByUserId(Long userId);

    // Find all lesson completions by lesson
    List<LessonCompletion> findByLessonId(Long lessonId);

    // Find all completed lessons by user and course
    @Query("SELECT lc FROM LessonCompletion lc WHERE lc.user.id = :userId AND lc.lesson.course.id = :courseId")
    List<LessonCompletion> findCompletedLessonsByUserAndCourse(Long userId, Long courseId);

    boolean existsByUserIdAndLessonId(Long userId, Long lessonId);

}
