package com.doftec.sitesketch.service

import com.doftec.sitesketch.model.Resume
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class Aiservice(private val restClient: RestClient) {
    fun callAi(resume: Resume): String {
        val objectMapper = jacksonObjectMapper()
        val resumeJson = objectMapper.writeValueAsString(resume)



        val codePrompt = getPrompt(resumeJson)

            val requestBody = mapOf(
            "model" to "deepseek/deepseek-r1-0528-qwen3-8b:free",
            "messages" to listOf(
                mapOf("role" to "user", "content" to codePrompt)
            ),
            "temperature" to 0.7
        )

        val response = restClient.post()
            .uri("/chat/completions")
            .body(requestBody)
            .retrieve()
            .toEntity(String::class.java)
            .body ?: ""


        return try {
            val mapper = jacksonObjectMapper()
            val root: JsonNode = mapper.readTree(response)
            root.path("choices")[0].path("message").path("content").asText()
        } catch (e: Exception) {
            "Error parsing response: ${e.message}"
        }
    }
    fun getPrompt(resumeJson: String): String{
        return """
You are an expert frontend developer and UI/UX designer.

Your task is to generate a fully functional, production-ready, single-page portfolio website that dynamically renders content from a local `resume.json` file.

🧾 Technologies:
• HTML5  
• Plain CSS (no frameworks)  
• Vanilla JavaScript  
• No external JS or CSS files — everything must be in one HTML file  
• Icons via CDN only (FontAwesome Free, Heroicons, or Iconify)
• Css shouls be correctly style


📁 Data Source:
All content must be fetched dynamically from `./resume.json`. Do not hardcode any content.

Schema includes (but not limited to):

```json
{
  "personalInformation": {
    "fullName": "", "email": "", "phoneNumber": "", "location": "", "linkedIn": "", "gitHub": "", "portfolio": "", "website": "", "media": ""
  },
  "summary": "",
  "skills": [{ "title": "", "level": "" }],
  "experience": [{ "jobTitle": "", "company": "", "location": "", "duration": "", "responsibilities": [""], "toolsUsed": [""], "media": "" }],
  "education": [{ "degree": "", "university": "", "location": "", "graduationYear": "", "gpa": "" }],
  "certifications": [{ "title": "", "issuingOrganization": "", "year": "", "credentialId": "", "credentialUrl": "" }],
  "projects": [{ "title": "", "description": "", "technologiesUsed": [""], "githubLink": "", "liveDemoLink": "", "media": "" }],
  "achievements": [{ "title": "", "year": "", "extraInformation": "" }],
  "publications": [{ "title": "", "publisher": "", "publicationDate": "", "url": "" }],
  "caseStudies": [{ "title": "", "client": "", "description": "", "outcome": "", "media": "" }],
  "licenses": [{ "title": "", "licenseNumber": "", "issuingOrganization": "", "issueDate": "", "expiryDate": "" }],
  "languages": [{ "name": "", "proficiency": "" }],
  "interests": [{ "name": "" }],
  "references": [{ "name": "", "position": "", "company": "", "email": "", "phone": "" }],
  "mediaGallery": [{ "fileName": "", "fileType": "", "description": "", "url": "" }]
}
🧩 Render Logic:
Render the following sections only if the data is present:

Order:
Hero → Summary → Skills → Experience → Projects → Education → Certifications → Achievements → Publications → Case Studies → Licenses → Languages → Interests → Media Gallery → References → Contact
✅ If not rendering the data , the heading must not render 
    e.g if projects section is empty its title should not be visible at all.
✅please do not render heading if data is not present or section is empty like i can see a heading as license with no information below it heading will only render if that section is rendering
✅ Always render "Contact", even if the others are empty.
✅ All other sections must be hidden if there's no data.
✅ Still include the JS & HTML structure in the code so that if resume.json updates later, it auto-renders correctly.


🧭 Navigation:
• Show links for up to 6 sections only (if they have data):
About, Work Experience, Projects, Skills, Education, Contact
• It must be user friendly , responsive , clear and beautifully
• Navigation must be sticky, mobile-friendly, and smooth scrolling.
• Use a hamburger menu on smaller screens.
• Section headers must be visible after clicking nav (consider nav offset).

📬 Contact Form:
• Fields: Name (required), Email (required), Subject, Message (required)
• Use mailto: with pre-filled fields
• Show toast on success (auto-dismiss after 3 sec)
• Disable send button while sending

🎨 Design Theme:
• Clean, icy glassmorphism aesthetic
• Use dark,= and visible for text and light color in background
• Frosted glass cards with subtle shadows
• Smooth fade-in and slide-up animations
• Hover scale/depth effects
• Cool modern font and consistent typography
• Responsive layout (desktop, tablet, mobile)
• Lazy-load media
• Apply css correctly 
• Do not forget to fully implement responsiveness 


✅ Example Rendering Structure:
Use this structure for each render function inside the .then(data => { ... }) block:

javascript
Copy
Edit
fetch("./resume.json").then(res => res.json()).then(data => {
    if (data.summary) renderSummary(data.personalInformation, data.summary);
    if (data.skills?.length) renderSkills(data.skills);
    if (data.experience?.length) renderExperience(data.experience);
    if (data.projects?.length) renderProjects(data.projects);
    if (data.education?.length) renderEducation(data.education);
    if (data.certifications?.length) renderCertifications(data.certifications);
    if (data.languages?.length) renderLanguages(data.languages);
    if (data.achievements?.length) renderAchievements(data.achievements);
    if (data.caseStudies?.length) renderCaseStudies(data.caseStudies);
    if (data.mediaGallery?.length) renderMediaGallery(data.mediaGallery);
    if (data.references?.length) renderReferences(data.references);
    if (data.interests?.length) renderInterests(data.interests);
    renderContact(); // Always
});
Each section like renderSummary(...) must create and inject content only if the corresponding data is present. Structure for all sections must still exist in the code (so they appear when updated in JSON).

📦 Performance:
• Safe DOM access with optional chaining
• No hardcoded values
• No undefined/null errors in console
• Lazy load images and media
• Minimize layout shift

📝 Deliverable:
Generate one full HTML file (no external assets), which:
• Uses only inline CSS + JS
• Starts with <!DOCTYPE html>
• Includes all render logic
• Uses icon CDN (FontAwesome Free, Iconify, or Heroicons only)
• Fully working and ready to deploy
• No comments or placeholder content
• No need for the user to edit any code ever

📁 Summary:
Return only a complete, working HTML5 code file that:
• Dynamically loads resume.json
• Renders only non-empty sections
• Auto-updates when JSON is updated
• Contains exactly 6 or fewer nav links
• Is mobile-first, animated, responsive, glass-themed
• Css must apply in code 
• Ready to deploy 
• Please check your response twice before returning
• Needs no setup, build step, or modification

Please generate code that correctly renders all sections dynamically from the JSON file and allows smooth navigation between them.


Now return only the code.
""".trimIndent()

    }
}