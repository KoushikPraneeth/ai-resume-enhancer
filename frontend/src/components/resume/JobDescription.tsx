
import { Card } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";

interface JobDescriptionProps {
  jobDescription: string;
  onJobDescriptionChange: (value: string) => void;
}

export default function JobDescription({ 
  jobDescription, 
  onJobDescriptionChange 
}: JobDescriptionProps) {
  return (
    <Card className="p-6 shadow-lg">
      <h2 className="text-2xl font-semibold mb-6">Job Description</h2>
      <Textarea
        placeholder="Paste the job description here..."
        value={jobDescription}
        onChange={(e) => onJobDescriptionChange(e.target.value)}
        className="min-h-[100px]"
      />
    </Card>
  );
}

