package com.ncnmo.aspire.elearning.repository;

import com.ncnmo.aspire.elearning.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserId(Long userId);
    List<Enrollment> findByCourseId(Long courseId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
