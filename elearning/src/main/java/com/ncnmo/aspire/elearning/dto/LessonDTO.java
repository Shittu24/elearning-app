package com.ncnmo.aspire.elearning.dto;

import lombok.Data;

@Data
public class LessonDTO {
    private Long id;
    private String title;
    private String textContent;  // This will hold text content
    private Long courseId;  // To associate the lesson with a course
}
