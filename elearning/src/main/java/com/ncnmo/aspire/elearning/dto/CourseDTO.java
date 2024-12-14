package com.ncnmo.aspire.elearning.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String imagePath;
    private List<LessonDTO> lessons;
}


