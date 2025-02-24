
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card } from "@/components/ui/card";

interface PersonalInfo {
  fullName: string;
  jobTitle: string;
  location: string;
  mobileNumber: string;
  email: string;
  linkedIn: string;
  github: string;
}

interface PersonalInfoFormProps {
  personalInfo: PersonalInfo;
  onPersonalInfoChange: (field: keyof PersonalInfo) => (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function PersonalInfoForm({ personalInfo, onPersonalInfoChange }: PersonalInfoFormProps) {
  return (
    <Card className="p-6 shadow-lg">
      <h2 className="text-2xl font-semibold mb-6">Personal Information</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="space-y-4">
          <div>
            <Label htmlFor="fullName">Full Name</Label>
            <Input
              id="fullName"
              value={personalInfo.fullName}
              onChange={onPersonalInfoChange("fullName")}
              placeholder="John Doe"
            />
          </div>
          <div>
            <Label htmlFor="jobTitle">Job Title</Label>
            <Input
              id="jobTitle"
              value={personalInfo.jobTitle}
              onChange={onPersonalInfoChange("jobTitle")}
              placeholder="Software Engineer"
            />
          </div>
          <div>
            <Label htmlFor="location">Location</Label>
            <Input
              id="location"
              value={personalInfo.location}
              onChange={onPersonalInfoChange("location")}
              placeholder="City, Country"
            />
          </div>
          <div>
            <Label htmlFor="mobileNumber">Mobile Number</Label>
            <Input
              id="mobileNumber"
              value={personalInfo.mobileNumber}
              onChange={onPersonalInfoChange("mobileNumber")}
              placeholder="+1 234 567 8900"
            />
          </div>
        </div>
        <div className="space-y-4">
          <div>
            <Label htmlFor="email">Email Address</Label>
            <Input
              id="email"
              type="email"
              value={personalInfo.email}
              onChange={onPersonalInfoChange("email")}
              placeholder="john.doe@example.com"
            />
          </div>
          <div>
            <Label htmlFor="linkedIn">LinkedIn Profile</Label>
            <Input
              id="linkedIn"
              value={personalInfo.linkedIn}
              onChange={onPersonalInfoChange("linkedIn")}
              placeholder="https://linkedin.com/in/johndoe"
            />
          </div>
          <div>
            <Label htmlFor="github">GitHub / Portfolio Website</Label>
            <Input
              id="github"
              value={personalInfo.github}
              onChange={onPersonalInfoChange("github")}
              placeholder="https://github.com/johndoe"
            />
          </div>
        </div>
      </div>
    </Card>
  );
}

