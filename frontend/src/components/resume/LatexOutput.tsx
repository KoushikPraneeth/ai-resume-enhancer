
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import TeX from '@matejmazur/react-katex';
import { useToast } from "@/components/ui/use-toast";

interface LatexOutputProps {
  latexCode: string;
  onLatexCodeChange: (value: string) => void;
}

export default function LatexOutput({ latexCode, onLatexCodeChange }: LatexOutputProps) {
  const { toast } = useToast();

  return (
    <Card className="p-6 shadow-lg space-y-4">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-semibold">LaTeX Output</h2>
        <Button onClick={() => {
          toast({
            title: "LaTeX Generated",
            description: "Your resume has been converted to LaTeX format.",
          });
        }}>Generate LaTeX</Button>
      </div>
      
      <div className="grid gap-6 md:grid-cols-2">
        <div className="space-y-4">
          <Label>LaTeX Code</Label>
          <Textarea
            value={latexCode}
            onChange={(e) => onLatexCodeChange(e.target.value)}
            className="min-h-[300px] font-mono text-sm"
          />
        </div>
        
        <div className="space-y-4">
          <Label>Preview</Label>
          <div className="min-h-[300px] p-4 bg-white rounded-md border overflow-auto">
            <TeX block>{latexCode}</TeX>
          </div>
        </div>
      </div>

      <Button 
        className="w-full mt-4" 
        onClick={() => {
          toast({
            title: "Download Started",
            description: "Your PDF is being generated...",
          });
        }}
      >
        Download PDF
      </Button>
    </Card>
  );
}

