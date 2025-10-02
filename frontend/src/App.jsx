import { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import './App.css';

function App() {
  // States for the core evaluation
  const [resume, setResume] = useState('');
  const [jobDescription, setJobDescription] = useState('');
  const [evaluationResult, setEvaluationResult] = useState('Your evaluation report will appear here...');
  const [isLoading, setIsLoading] = useState(false);
  const [copyButtonText, setCopyButtonText] = useState('Copy Report');

  // States for the interview question feature
  const [isGeneratingQuestions, setIsGeneratingQuestions] = useState(false);
  const [interviewQuestions, setInterviewQuestions] = useState('');
  const [evaluationDone, setEvaluationDone] = useState(false);

  // Handles the main resume evaluation
  const handleSubmit = async () => {
    setIsLoading(true);
    setEvaluationDone(false); // Reset on new submission
    setInterviewQuestions(''); // Clear old questions
    setCopyButtonText('Copy Report');
    setEvaluationResult('Evaluating your resume against the job description...');
    try {
      const response = await fetch('http://localhost:8080/api/evaluate-resume', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ resume, jobDescription }),
      });

      const data = await response.json();
      if (!response.ok) throw new Error(data.evaluation || 'An unknown error occurred during evaluation');
      
      setEvaluationResult(data.evaluation);
      setEvaluationDone(true); // Mark evaluation as complete to show the next feature button

    } catch (error) {
      console.error('Error:', error);
      setEvaluationResult(`Failed to evaluate resume. Details: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  };
  
  // Handles the interview question generation
  const handleGenerateQuestions = async () => {
    setIsGeneratingQuestions(true);
    setInterviewQuestions('Generating tailored interview questions...');
    try {
      const response = await fetch('http://localhost:8080/api/generate-questions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ resume, jobDescription }),
      });

      const data = await response.json();
      if (!response.ok) throw new Error(data.questions || 'An unknown error occurred during question generation');

      setInterviewQuestions(data.questions);

    } catch (error) {
      console.error('Error generating questions:', error);
      setInterviewQuestions(`Failed to generate questions. Details: ${error.message}`);
    } finally {
      setIsGeneratingQuestions(false);
    }
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(evaluationResult).then(() => {
      setCopyButtonText('Copied!');
      setTimeout(() => setCopyButtonText('Copy Report'), 2000);
    }, () => {
      setCopyButtonText('Failed!');
    });
  };

  return (
    <>
      <div className="background-aurora"></div>
      <div className="app-container">
        
        <div className="nav-panel">
          <div>
            <h1 style={{ textAlign: 'left', fontSize: '1.8rem', color: 'var(--accent-cyan)', marginBottom: '2rem' }}>
              AI ATS Evaluator
            </h1>
          </div>
          <button
            className="btn btn-primary make-button"
            onClick={handleSubmit}
            disabled={isLoading || !resume || !jobDescription}
          >
            {isLoading ? 'Evaluating...' : 'Evaluate My Resume'}
          </button>
        </div>

        <div className="editor-panel">
          <div className="form-section">
            <div className="form-group">
              <h2>Your Resume</h2>
              <textarea
                value={resume}
                onChange={(e) => setResume(e.target.value)}
                placeholder="Paste your full resume here..."
              />
            </div>
            <div className="form-group">
              <h2>Job Description</h2>
              <textarea
                value={jobDescription}
                onChange={(e) => setJobDescription(e.target.value)}
                placeholder="Paste the job description here..."
              />
            </div>
          </div>
        </div>

        <div className="preview-panel">
          <div className="preview-header">
            <h2>Evaluation Report</h2>
            <div className="download-buttons">
              <button onClick={handleCopy} disabled={!evaluationResult || isLoading} className="btn btn-primary">
                {copyButtonText}
              </button>
            </div>
          </div>
          
          <div id="preview-content" className="markdown-preview">
            <ReactMarkdown>{evaluationResult}</ReactMarkdown>
          </div>
          
          {/* This section only appears after a successful evaluation */}
          {evaluationDone && !isLoading && (
            <div className="feature-section">
              <button 
                onClick={handleGenerateQuestions} 
                disabled={isGeneratingQuestions} 
                className="btn btn-primary make-button"
              >
                {isGeneratingQuestions ? 'Generating...' : '✨ Generate Interview Questions'}
              </button>
              
              {interviewQuestions && (
                <div id="questions-content" className="markdown-preview">
                  <ReactMarkdown>{interviewQuestions}</ReactMarkdown>
                </div>
              )}
            </div>
          )}
        </div>

      </div>
    </>
  );
}

export default App;