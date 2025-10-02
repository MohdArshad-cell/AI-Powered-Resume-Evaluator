# AI-Powered ATS Resume Evaluator

A full-stack web application that acts as a personal career coach, leveraging Google's Gemini AI to provide a detailed, score-based evaluation of a resume against a job description, complete with actionable feedback and tailored interview questions.

## ✨ Core Features

This application goes beyond simple keyword matching to provide a comprehensive analysis that helps job seekers pass automated screening and impress recruiters.

### 📈 ATS Score & Evaluation
- Calculates a precise 1–10 match score, simulating how an Applicant Tracking System (ATS) would rank the resume.
- Generates a detailed feedback report outlining strengths, weaknesses, and key missing terms.

### ✍️ Actionable Improvement Suggestions
- Provides specific, line-by-line suggestions for rephrasing resume bullet points to be more impactful and keyword-rich.
- Shows **"Original" vs. "Suggested"** text to make improvements clear and easy to implement.

### 🎙️ Interview Question Generator
- After the evaluation, the app can generate a list of 5–7 tailored interview questions.
- Questions are categorized into **Technical**, **Project-based**, and **Behavioral** to help candidates prepare effectively.

## 🛠️ How It Works & Tech Stack

The application uses a robust three-tier architecture and a sophisticated multi-step AI prompting strategy to ensure high-quality, reliable results.

| Layer     | Tech Stack |
|-----------|------------|
| Frontend  | React.js (Vite), CSS3, react-markdown |
| Backend   | Java 17+, Spring Boot, Maven |
| AI Core   | Python 3.8+, Google Gemini Pro API |

---

## 🚀 Getting Started

Follow these instructions to get the project running on your local machine.

### Prerequisites
- Java 17+ and Maven  
- Node.js and npm  
- Python 3.8+  
- A Google Gemini API Key from Google AI Studio  

### 1️⃣ Backend Setup (evaluator-api)
```bash
# Navigate to the backend folder
cd resume-backend/evaluator-api

### 2️⃣ AI Script Configuration
# Navigate to the Python scripts directory
cd src/main/resources/scripts/

# Install the required Python library
pip install google-generativeai

API_KEY = os.getenv("GEMINI_API_KEY", "YOUR_ACTUAL_API_KEY_GOES_HERE")

3️⃣ Frontend Setup
# Navigate to the frontend folder from the root directory
cd frontend

# Install all necessary npm packages
npm install

4️⃣ Running the Application
mvn spring-boot:run

npm run dev

# Build the project using Maven
mvn clean install
