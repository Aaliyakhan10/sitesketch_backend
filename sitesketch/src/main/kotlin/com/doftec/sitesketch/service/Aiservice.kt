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
            "model" to "deepseek/deepseek-r1-0528:free",
            "messages" to listOf(
                mapOf("role" to "user", "content" to codePrompt)
            ),
            "temperature" to 0.9
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
You are an expert front-end developer and UI/UX designer.

Your task is to generate a **production-ready**, fully responsive **single-page portfolio website** using **only a single HTML file**. This website must dynamically render all content based on data loaded from a local `resume.json` file.

---

### 🔧 Technologies & Constraints:

* HTML5
* Plain CSS (no frameworks)
* Vanilla JavaScript
* **All styles and scripts must be inline** — no external files
* **Icons allowed via CDN only** (FontAwesome Free, Heroicons, or Iconify)

---

### 📄 Data Source & Fallback:

* Primary: Fetch JSON data from `./resume.json`
* Fallback: Use JSON from `localStorage.getItem("resumeData")` (parsed into `resumeData`)
* Display a user-friendly error message if no data is available or loading fails

---

### 🔍 Rendering Logic:

Render the following sections **only if data exists** in `resume.json` or `resumeData`, in this exact order:

1. Hero
2. Summary
3. Skills
4. Experience
5. Projects
6. Education
7. Certifications
8. Achievements
9. Publications
10. Case Studies
11. Licenses
12. Languages
13. Interests
14. Media Gallery
15. References
16. Contact (Always render)
> Omit both section and heading for any section with no data.

---

### 🧭 Navigation:

* Must be present and support smooth scrolling
* Display links to **a maximum of 6 sections** if those sections have data:

  * About, Work Experience, Projects, Skills, Education, Contact
* Sticky, mobile-friendly, and clear design
* Hamburger menu for small screens
* Offset header height for in-page anchor jumps
*The entire site will be loaded inside an <iframe>.
❗️Do not write navigation that reloads or navigates the iframe source. All section links must scroll smoothly within the iframe itself.

---

### 📬 Contact Form:

* Always rendered
* Fields: Name (required), Email (required), Subject, Message (required)
* Uses `mailto:` with prefilled content
* Shows a toast notification on successful send
* Disables send button during submission
* Simple, minimal, and reliable — no third-party services

---

### 🎨 Design Theme:

* **Modern glassmorphism aesthetic**
* Dark text on bright/light background
* Frosted glass cards with shadows
* Smooth fade-in and slide-up animations
* Hover scale and depth effects
* Clean, readable typography
* Fully responsive for desktop, tablet, and mobile
* Lazy-load media assets
* Icons where useful — **avoid emoji-only or overuse of icons**

---

### ✅ Example Render Logic (inside `.then(data => {})` block):

```js
if (data.summary) renderSummary(data.personalInformation, data.summary);
if (data.skills?.length) renderSkills(data.skills);
if (data.experience?.length) renderExperience(data.experience);
if (data.projects?.length) renderProjects(data.projects);
if (data.education?.length) renderEducation(data.education);
if (data.certifications?.length) renderCertifications(data.certifications);
if (data.achievements?.length) renderAchievements(data.achievements);
if (data.publications?.length) renderPublications(data.publications);
if (data.caseStudies?.length) renderCaseStudies(data.caseStudies);
if (data.licenses?.length) renderLicenses(data.licenses);
if (data.languages?.length) renderLanguages(data.languages);
if (data.interests?.length) renderInterests(data.interests);
if (data.mediaGallery?.length) renderMediaGallery(data.mediaGallery);
if (data.references?.length) renderReferences(data.references);
renderContact(); // Always render
```
please write code carefully so its will not throw error like "Error loading resume data" or "Error loading resume data"
> All section-rendering functions must be included, even if data is currently missing.
> Code should be structured so updates to `resume.json` automatically render new sections if populated.

---

### 🧪 Performance & Code Quality:

* Use optional chaining `?.` throughout
* Parse localStorage data with `JSON.parse()`
* Avoid accessing nested values without checks
* Validate fetch status and response format before parsing
* Lazy-load images and avoid layout shifts
* Gracefully handle all possible errors
* No undefined/null console errors
*The entire site will be loaded inside an <iframe>.
❗️Do not write navigation that reloads or navigates the iframe source. All section links must scroll smoothly within the iframe itself.
---

### 📝 Deliverable Requirements:

The output must be a **single HTML file** that:

* Starts with `<!DOCTYPE html>`
* Contains inline CSS and JS only
* Renders all sections dynamically from `resume.json`
* Renders no section or heading for empty data
* Limits navigation to 6 data-present sections
* Fully responsive, animated, and styled correctly
* No hardcoded content or placeholders
* Requires **no build tools, packages, or setup**
* Includes a footer: **"made with love by sitesketch"**
* Ready for immediate deployment and iframe embedding
* Avoid showing this on code instead hide that field/section 
undefined - undefined (undefined)
undefined - undefined (undefined)
undefined - undefined (undefined)
undefined - undefined (undefined)
---

### 📦 Final Instruction:

Return only the final, complete, minified, and production-ready HTML5 code file — nothing else. Make sure all instructions above are followed precisely.

""".trimIndent()

    }
}