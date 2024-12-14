package com.ncnmo.aspire.elearning.repository;

import com.ncnmo.aspire.elearning.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c JOIN FETCH c.lessons l")
    List<Course> findAllWithLessons();

    // Custom query to find the course by lessonId
    @Query("SELECT c FROM Course c JOIN c.lessons l WHERE l.id = :lessonId")
    Course findByLessonId(@Param("lessonId") Long lessonId);

}
