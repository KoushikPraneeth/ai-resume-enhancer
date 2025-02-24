
import ResumeForm from "@/components/ResumeForm";

const Index = () => {
  return (
    <div className="min-h-screen bg-slate-50">
      <header className="w-full py-6 bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-6">
          <h1 className="text-3xl font-semibold text-slate-900">Resume Renewal Lab</h1>
          <p className="text-slate-600 mt-2">Enhance your resume with AI precision</p>
        </div>
      </header>
      <main className="py-8">
        <ResumeForm />
      </main>
    </div>
  );
};

export default Index;
