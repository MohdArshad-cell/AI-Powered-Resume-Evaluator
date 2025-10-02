package com.airesume.evaluatorapi.service;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PythonScriptService {

    public String runEvaluationScript(String resume, String jobDescription) {
        try {
            String pythonExecutable = "python";
            // Make sure your new python script is named 'evaluate.py'
            File scriptFile = ResourceUtils.getFile("classpath:scripts/evaluate.py");
            String scriptPath = scriptFile.getAbsolutePath();

            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath, resume, jobDescription);
            
            Process process = processBuilder.start();

            String output = new BufferedReader(
                new InputStreamReader(process.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

            if (!process.waitFor(5, TimeUnit.MINUTES)) {
                process.destroy();
                throw new RuntimeException("Python script execution timed out.");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                String errorOutput = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));
                throw new RuntimeException("Python script exited with error code " + exitCode + ". Error: " + errorOutput);
            }

            return output;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Could not process the evaluation. " + e.getMessage();
        }
    }

    // Add this new method inside your PythonScriptService class

public String runQuestionGeneratorScript(String resume, String jobDescription) {
    try {
        String pythonExecutable = "python";
        // This will call our new Python script
        File scriptFile = ResourceUtils.getFile("classpath:scripts/generate_questions.py");
        String scriptPath = scriptFile.getAbsolutePath();

        ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath, resume, jobDescription);
        
        Process process = processBuilder.start();

        String output = new BufferedReader(
            new InputStreamReader(process.getInputStream()))
            .lines()
            .collect(Collectors.joining("\n"));

        if (!process.waitFor(5, TimeUnit.MINUTES)) {
            process.destroy();
            throw new RuntimeException("Python script execution timed out.");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            String errorOutput = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))
                .lines()
                .collect(Collectors.joining("\n"));
            throw new RuntimeException("Python script exited with error code " + exitCode + ". Error: " + errorOutput);
        }

        return output;

    } catch (Exception e) {
        e.printStackTrace();
        return "Error: Could not generate questions. " + e.getMessage();
    }
}
}