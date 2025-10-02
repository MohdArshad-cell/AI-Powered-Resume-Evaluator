package com.airesume.evaluatorapi.dto;

import lombok.Data;

@Data
public class EvaluationRequest {
    private String resume;
    private String jobDescription;
}