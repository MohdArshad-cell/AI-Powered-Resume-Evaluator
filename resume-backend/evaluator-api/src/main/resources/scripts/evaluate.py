import sys
import os
import json
import re
import google.generativeai as genai
import contextlib

# --- CONFIGURATION ---
# IMPORTANT: Replace "YOUR_API_KEY_HERE" with your actual Google Gemini API key
# Or, set it as an environment variable named GEMINI_API_KEY
API_KEY = os.getenv("GEMINI_API_KEY", "AIzaSyDzlT-wD0owETwpqiAnQ0UfhDWf6Ug40WQ")
MODEL_NAME = "gemini-2.5-flash-lite"

# --- HELPER FUNCTIONS ---

def load_file(file_path: str) -> str:
    """Reads and returns the content of a file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return f.read()
    except FileNotFoundError:
        # Provide a clearer error message back to the Spring Boot service
        print(f"Error: Prompt file not found at {file_path}", file=sys.stderr)
        sys.exit(1)

@contextlib.contextmanager
def suppress_stderr():
    """A context manager to temporarily suppress stderr to hide library warnings."""
    original_stderr = sys.stderr
    sys.stderr = open(os.devnull, 'w', encoding='utf-8')
    try:
        yield
    finally:
        sys.stderr.close()
        sys.stderr = original_stderr

def call_gemini_api(prompt: str) -> str:
    """Calls the Gemini API with a given prompt and returns the text response."""
    with suppress_stderr():
        genai.configure(api_key=API_KEY)
        model = genai.GenerativeModel(model_name=MODEL_NAME)
        response = model.generate_content(prompt)
    return response.text

def clean_json_string(json_string: str) -> str:
    """Finds and extracts the first valid JSON object or array from a string."""
    # This regex is improved to handle markdown code blocks (```json ... ```)
    match = re.search(r'```(json)?\s*(\{[\s\S]*\}|\[[\s\S]*\])\s*```', json_string)
    if match:
        return match.group(2) # Return the JSON part
    
    # Fallback for raw JSON
    match = re.search(r'\{[\s\S]*\}|\[[\s\S]*\]', json_string)
    if match:
        return match.group(0)
    
    raise ValueError(f"No valid JSON object or array found in the AI's response: {json_string}")


def main():
    """Main execution function to run the 3-step evaluation."""
    if len(sys.argv) != 3:
        print("Error: Invalid arguments. Script requires a resume and a job description.", file=sys.stderr)
        sys.exit(1)

    if not API_KEY or API_KEY == "YOUR_API_KEY_HERE":
        print("Error: Gemini API key is not configured in evaluate.py. Please add your key.", file=sys.stderr)
        sys.exit(1)

    resume_content = sys.argv[1]
    job_description = sys.argv[2]
    script_dir = os.path.dirname(__file__)

    try:
        # STEP 1: Analyze the Job Description
        prompt1_template = load_file(os.path.join(script_dir, 'prompt_step1_jd_analysis.txt'))
        prompt1 = prompt1_template.format(job_description=job_description)
        jd_analysis_str = call_gemini_api(prompt1)
        jd_analysis_json = json.loads(clean_json_string(jd_analysis_str))

        # STEP 2: Analyze the Resume
        prompt2_template = load_file(os.path.join(script_dir, 'prompt_step2_resume_analysis.txt'))
        prompt2 = prompt2_template.format(resume_content=resume_content)
        resume_analysis_str = call_gemini_api(prompt2)
        resume_analysis_json = json.loads(clean_json_string(resume_analysis_str))

        # STEP 3: Perform the Comprehensive ATS Evaluation
        prompt3_template = load_file(os.path.join(script_dir, 'prompt_step3_ats_evaluation.txt'))
        prompt3 = prompt3_template.format(
            job_description_json=json.dumps(jd_analysis_json, indent=2),
            resume_json=json.dumps(resume_analysis_json, indent=2),
            original_resume=resume_content
        )
        final_evaluation = call_gemini_api(prompt3)
        
        # The final report is printed to standard output, which Java will capture
        print(final_evaluation)

    except Exception as e:
        print(f"A critical error occurred during the evaluation process: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()