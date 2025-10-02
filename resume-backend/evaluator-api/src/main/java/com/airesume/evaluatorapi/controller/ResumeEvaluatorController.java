package com.airesume.evaluatorapi.controller;

import com.airesume.evaluatorapi.dto.EvaluationRequest;
import com.airesume.evaluatorapi.dto.EvaluationResponse;
import com.airesume.evaluatorapi.dto.QuestionResponse; // <-- Make sure this is imported
import com.airesume.evaluatorapi.service.PythonScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ResumeEvaluatorController {

    @Autowired
    private PythonScriptService pythonScriptService;

    // This is your original, working endpoint
    @PostMapping("/evaluate-resume")
    public ResponseEntity<EvaluationResponse> evaluateResume(@RequestBody EvaluationRequest request) {
        String evaluationResult = pythonScriptService.runEvaluationScript(
                request.getResume(),
                request.getJobDescription()
        );
        EvaluationResponse response = new EvaluationResponse();
        response.setEvaluation(evaluationResult);
        return ResponseEntity.ok(response);
    }

    // --- THIS IS THE NEW ENDPOINT TO VERIFY ---
    @PostMapping("/generate-questions")
    public ResponseEntity<QuestionResponse> generateQuestions(@RequestBody EvaluationRequest request) {
        String questionsResult = pythonScriptService.runQuestionGeneratorScript(
                request.getResume(),
                request.getJobDescription()
        );
        QuestionResponse response = new QuestionResponse();
        response.setQuestions(questionsResult);
        return ResponseEntity.ok(response);
    }
}