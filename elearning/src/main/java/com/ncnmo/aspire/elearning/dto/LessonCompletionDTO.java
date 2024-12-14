package com.ncnmo.aspire.elearning.dto;

import lombok.Data;

@Data
public class LessonCompletionDTO {
    private Long id;
    private Long userId;
    private Long lessonId;
    private boolean completed;
}
