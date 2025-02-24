
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { useToast } from "@/components/ui/use-toast";
import 'katex/dist/katex.min.css';
import PersonalInfoForm from "./resume/PersonalInfoForm";
import ResumeSection from "./resume/ResumeSection";
import JobDescription from "./resume/JobDescription";
import LatexOutput from "./resume/LatexOutput";

interface PersonalInfo {
  fullName: string;
  jobTitle: string;
  location: string;
  mobileNumber: string;
  email: string;
  linkedIn: string;
  github: string;
}

interface Section {
  id: string;
  title: string;
  content: string;
  enhanced: string;
}

export default function ResumeForm() {
  const { toast } = useToast();
  const [personalInfo, setPersonalInfo] = useState<PersonalInfo>({
    fullName: "",
    jobTitle: "",
    location: "",
    mobileNumber: "",
    email: "",
    linkedIn: "",
    github: "",
  });

  const [sections, setSections] = useState<Section[]>([
    { id: "summary", title: "Summary", content: "", enhanced: "" },
    { id: "skills", title: "Skills", content: "", enhanced: "" },
    { id: "experience", title: "Work Experience", content: "", enhanced: "" },
    { id: "education", title: "Education", content: "", enhanced: "" },
    { id: "certifications", title: "Certifications", content: "", enhanced: "" },
    { id: "achievements", title: "Achievements", content: "", enhanced: "" },
  ]);

  const [jobDescription, setJobDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [isEnhanced, setIsEnhanced] = useState(false);
  const [latexCode, setLatexCode] = useState(`\\documentclass{resume}
\\usepackage{graphicx}
\\begin{document}
\\name{Your Name}
\\begin{resume}

% Content will be populated here

\\end{resume}
\\end{document}`);

  const handlePersonalInfoChange = (field: keyof PersonalInfo) => (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setPersonalInfo((prev) => ({
      ...prev,
      [field]: e.target.value,
    }));
  };

  const enhanceAllSections = async () => {
    setLoading(true);
    try {
      await new Promise((resolve) => setTimeout(resolve, 2000));
      setSections(sections.map(section => ({
        ...section,
        enhanced: `Enhanced ${section.title} content`
      })));
      setIsEnhanced(true);
      toast({
        title: "Enhancement Complete",
        description: "All sections have been improved by AI.",
      });
    } catch (error) {
      toast({
        title: "Enhancement Failed",
        description: "Please try again later.",
        variant: "destructive",
      });
    }
    setLoading(false);
  };

  const redoEnhancement = async (sectionId: string) => {
    const sectionToUpdate = sections.find(section => section.id === sectionId);
    if (!sectionToUpdate) return;

    try {
      await new Promise((resolve) => setTimeout(resolve, 1000));
      setSections(sections.map(section => 
        section.id === sectionId 
          ? { ...section, enhanced: `Re-enhanced ${section.title} content` }
          : section
      ));
      toast({
        title: "Section Re-enhanced",
        description: "New suggestions have been generated.",
      });
    } catch (error) {
      toast({
        title: "Enhancement Failed",
        description: "Please try again later.",
        variant: "destructive",
      });
    }
  };

  const generateLatex = () => {
    const enhancedSections = sections
      .filter(section => section.enhanced)
      .map(section => section.enhanced)
      .join('\n\n');

    const newLatexCode = `\\documentclass{resume}
\\usepackage{graphicx}
\\begin{document}
\\name{${personalInfo.fullName}}
\\begin{resume}

${enhancedSections}

\\end{resume}
\\end{document}`;

    setLatexCode(newLatexCode);
  };

  return (
    <div className="w-full max-w-4xl mx-auto p-6 space-y-8 animate-fadeIn">
      <JobDescription 
        jobDescription={jobDescription}
        onJobDescriptionChange={setJobDescription}
      />

      <PersonalInfoForm 
        personalInfo={personalInfo}
        onPersonalInfoChange={handlePersonalInfoChange}
      />

      {sections.map((section) => (
        <ResumeSection
          key={section.id}
          section={section}
          isEnhanced={isEnhanced}
          onContentChange={(content) =>
            setSections(
              sections.map((s) =>
                s.id === section.id ? { ...s, content } : s
              )
            )
          }
          onRedoEnhancement={() => redoEnhancement(section.id)}
        />
      ))}

      <Button
        onClick={enhanceAllSections}
        disabled={loading || sections.every(section => !section.content)}
        className="w-full mt-8"
      >
        {loading ? "Enhancing..." : "Enhance All Sections"}
      </Button>

      {isEnhanced && (
        <LatexOutput 
          latexCode={latexCode}
          onLatexCodeChange={setLatexCode}
        />
      )}
    </div>
  );
}

