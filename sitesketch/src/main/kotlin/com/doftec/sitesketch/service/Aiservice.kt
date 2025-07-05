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
            "model" to "deepseek/deepseek-chat-v3-0324:free",
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
You are an expert frontend developer and UI/UX designer. Create a modern, production-ready, single-page portfolio website that dynamically renders all content from a local resume.json file.
You are an expert frontend developer and creative UI/UX designer.

Your task is to generate a fully functional, production-ready, single-page portfolio website that dynamically renders content based on a local JSON file.

🚀 Objective:
Build a beautiful, modern, mobile-first, fully responsive, single-page portfolio using:

HTML

Tailwind CSS (via CDN)

Vanilla JavaScript (no frameworks)

All content must be rendered dynamically from a local resume.json file (same directory).
The data provide by user is 
 🔽 Resume JSON:
        $resumeJson
        Use this data to know about profession and style the web page accordingly.

📁 Data Source:
Fetch data from ./resume.json. The schema is:

json
Copy
Edit
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
  "languages": [{ "id": null, "name": "", "proficiency": "" }],
  "interests": [{ "id": null, "name": "" }],
  "references": [{ "name": "", "position": "", "company": "", "email": "", "phone": "" }],
  "mediaGallery": [{ "fileName": "", "fileType": "", "description": "", "url": "" }]
}
🚀 Core Objectives
100% Dynamic: All content must be fetched and rendered from ./resume.json. No hardcoded values allowed.
Understand the json structure provided and generate a condition based visibility of each section present in structure

Auto-Adaptive: The site should automatically reflect changes or additions to the JSON without requiring any code edits.

Deployment-Ready: The output must be a single, complete HTML file. The user should only need to drop this HTML file and the JSON into the same folder — nothing else.

Zero User Edits: Do not assume the user will modify or configure any code. Everything must work immediately and independently.

📦 Technical Specifications
Single HTML file only — no external JS/CSS files.

Tailwind CSS v3.3 via CDN.

Vanilla JavaScript for all logic and rendering.

No build step required (pure HTML + JS + CDN).

Dynamic Navigation:

Only show links for visible, data-backed sections.

New sections added to JSON should appear automatically in both content and nav.

Safe Rendering:

Do not attempt to render empty/missing sections.

Use optional chaining, safe DOM manipulation, and robust JSON validation.
Zero User Edits: Do not assume the user will modify or configure any code. Everything must work immediately and independently.

Fully Mobile Responsive: The layout, navigation, and content must be optimized for all screen sizes, including phones and tablets.

Hamburger Menu on Mobile: On smaller screens, display a hamburger menu that toggles the navigation.
Adjust the layout according to device size
Add only and only 7 section in navigation not more than that put about,contact,work experience ,project , achievements,education  and skills only ,no other then this if this is not empty if empty then add according to need or profession .
Code should not throw any error please check this.
🧩 Dynamic Sections (Render only if data exists)
Render each section only if data is present in resume.json. Always render contact.

Render sections in this order:

Hero (Name, Title, Socials)
hero → summary → skills → experience → projects → education → certifications → achievements → publications → caseStudies → licenses → languages → interests → mediaGallery → references → contact


Contact (Always)

⚠️ Even if a section is empty, its HTML and JS must still exist in the codebase (so it can render if JSON is updated later). But do not render or display the section or nav item if it has no data.

📬 Contact Form
Fields: Name (required), Email (required), Subject, Message (required)

Use mailto: handler with pre-filled data

Show a success toast on submission (auto-dismiss)

Disable button while sending

🎨 Design Specifications
Visual Theme: Clean, modern, icy aesthetic

Color Palette:

Primary: According to you

Secondary:According to you


Accent: According to you


Background: According to you
Add Glass effect 
icy/glassmorphism theme

Effects:

Frosted glass cards with subtle shadows

Animated fade-in / slide-up elements

Hover effects with depth or scaling

Smooth scrolling and nav animations
3d effect

Typography:

Consistent font sizing, spacing, and alignment

Clear mobile-first layout and responsiveness

✅ ICON REQUIREMENTS:
• **All icons must come from a reliable free icon CDN or service like [Heroicons](https://heroicons.com), [Font Awesome Free](https://fontawesome.com/icons?d=free), or [Iconify](https://iconify.design).**
• Do **NOT** use AI-generated icons, base64-embedded icons, or broken SVG links.
• Ensure all icons load properly on first page load without requiring any local setup.
• Use appropriate icons for navigation, skills, contact form, sections, etc.

Navigation:

Sticky top nav/stick to left side of screen (top or left-side)

Auto-hiding on scroll

Mobile hamburger menu with Tailwind 

✅ Functional Requirements
Fully Dynamic Navigation

Only shows sections that exist in the JSON

Automatically adapts to added/removed sections
Automatically add/remove nav link if section is present first and json updated and section is empty it should not be present in nav and only five 6 link in nav all section link is horrible
Add navigation link based on condition

Auto-rendered Content

All content must come from resume.json

No user code changes required for updates

Robust Error Handling

Validate JSON structure before rendering

Fallback UI for missing/invalid data

Avoid "Cannot set properties of null" and similar errors

Performance Optimizations

Lazy loading images and videos

Smooth animations with minimal layout shift (CLS)

Efficient rendering and event delegation

📝 Output Requirements
One clean, complete HTML5 file

Includes:

Tailwind CDN

Heroicons CDN

All rendering and logic in inline JavaScript

Must begin with <!DOCTYPE html>

No:

Comments

Placeholder values

Hardcoded resume content

Frameworks (React, Vue, etc.)

External scripts or stylesheets

🛑 Forbidden
Hardcoded resume data

Empty sections rendered visually

External JS or CSS files

Sky blue or cartoonish color schemes

User needing to touch or modify the code

📁 Summary:
The result should be a fully responsive, glassmorphic single-page portfolio that loads dynamically from resume.json, with no setup, edits, or configuration required by the user. Just drop the HTML and JSON into the same folder, and it works.""".trimIndent()

    }
}