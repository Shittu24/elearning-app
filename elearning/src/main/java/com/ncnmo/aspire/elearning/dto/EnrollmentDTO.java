package com.ncnmo.aspire.elearning.dto;

import lombok.Data;

@Data
public class EnrollmentDTO {
    private Long id;
    private Long userId;
    private Long courseId;
    private boolean completed;
}
