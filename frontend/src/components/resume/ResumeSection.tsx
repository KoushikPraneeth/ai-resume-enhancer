
import { Card } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { RotateCcw } from "lucide-react";

interface Section {
  id: string;
  title: string;
  content: string;
  enhanced: string;
}

interface ResumeSectionProps {
  section: Section;
  isEnhanced: boolean;
  onContentChange: (content: string) => void;
  onRedoEnhancement: () => void;
}

export default function ResumeSection({
  section,
  isEnhanced,
  onContentChange,
  onRedoEnhancement,
}: ResumeSectionProps) {
  return (
    <Card className="p-6 shadow-lg space-y-4">
      <h2 className="text-2xl font-semibold">{section.title}</h2>
      <div className="grid gap-6 md:grid-cols-2">
        <div className="space-y-4">
          <Label>Content</Label>
          <Textarea
            value={section.content}
            onChange={(e) => onContentChange(e.target.value)}
            placeholder={`Enter your ${section.title.toLowerCase()}...`}
            className="min-h-[150px]"
          />
        </div>

        {isEnhanced && (
          <div className="space-y-4 animate-fadeIn">
            <div className="flex justify-between items-center">
              <Label>Enhanced Content</Label>
              <Button
                variant="ghost"
                size="icon"
                onClick={onRedoEnhancement}
              >
                <RotateCcw className="h-4 w-4" />
              </Button>
            </div>
            <Textarea
              value={section.enhanced}
              readOnly
              className="min-h-[150px] bg-secondary"
            />
          </div>
        )}
      </div>
    </Card>
  );
}

