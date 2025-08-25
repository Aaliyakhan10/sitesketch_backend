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



        val codePrompt = getPrompt(resume)

            val requestBody = mapOf(
            "model" to "z-ai/glm-4.5-air:free",
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
    fun getPrompt(resumeJson: Resume): String{
        return """
ROLE, PURPOSE, AND OUTCOME
You are a Senior Front-End Developer and UI/UX Designer tasked with building a production-quality, single-page portfolio website with the following attributes:
•	The site is 100% dynamic: all content must be sourced from a structured JSON resume file (resume.json) or from browser localStorage under the key resumeData. No hardcoded data.
•	Styled with elegant, modern dark/bold and beautiful with decent color pallet  glassmorphism principles: translucent blurred panels, subtle shadowing, and sophisticated color palettes that evoke high-end aesthetics.
•	Fully responsive and mobile-first: flawlessly scales and renders across all device sizes — phones, tablets, laptops, desktops — with progressive enhancement.
•	Supports embedding inside iframes completely; navigation uses only in-page anchor links with smooth scrolling, never triggers page reload or external navigation.
•	Guarantees zero console errors/warnings at all times; UI never shows undefined, “null”, or placeholder text.
•	Every possible resume section (skills, projects, certifications, galleries, etc.) has a renderer, but only sections with valid, meaningful data are displayed.
•	Includes animated entrances and transitions on all sections and key UI elements — for example, hero headline typing effect, staggered skill animations, fade/slide effect on cards.
•	Ships as a single self-contained HTML file: all CSS and JavaScript must be inlined; external dependencies limited strictly to icon CDNs (FontAwesome/Iconify/Heroicons) to load icons only.
•	Adheres to WCAG 2.1 AA accessibility guidelines across all modern browsers and devices, including screen reader friendly semantic HTML and ARIA.
•	The footer must always be visible (fixed at bottom on desktop) with text:
made with ❤️ by sitesketch.
________________________________________
⚙️ CORE TECHNICAL & DESIGN SPECIFICATIONS
1. Technologies & File Structure
•	HTML5 only using semantic elements and ARIA attributes where needed.
•	Pure CSS for styling — no frameworks/preprocessors; styles are inline in a <style> block.
•	Vanilla ES6+ JavaScript only, contained in a <script> block at the end of the file.
•	No other external scripts except for icon font CDNs (FontAwesome, Iconify, Heroicons).
•	Data retrieval attempts fetch('./resume.json') first; if not present/invalid, fallback reads browser localStorage.getItem('resumeData').
•	If both missing/invalid: display a clear message instructing the user how to add data.
2. Data Loading & Validation
•	Use robust validation functions before rendering any section to ensure meaningful content exists.
•	Sample general validation helpers:
js
function hasData(arr) {
  if (!Array.isArray(arr)) return false;
  return arr.some(item => Object.values(item).some(val =>
    Array.isArray(val) ? val.some(e => typeof e === "string" ? e.trim() !== "" : false) : typeof val === "string" ? val.trim() !== "" : !!val
  ));
}
function isNotEmpty(obj) {
  return obj && Object.values(obj).some(v => typeof v === "string" ? v.trim() !== "" : !!v);
}
•	For sections with nested arrays (e.g., projects, experience), implement more granular per-item validation to skip empty entries.
3. JSON Resume Data Schema
Must support these comprehensive sections (extendable):
js
{
  personalInformation: {
    fullName, professionalTitle, email, phoneNumber, location,
    linkedIn, gitHub, portfolio, summary, media
  },
  skills: [{ title }],
  experience: [{ jobTitle, company, location, duration, responsibilities: [], toolsUsed: [], media }],
  projects: [{ title, description, technologiesUsed: [], githubLink, liveDemoLink, media }],
  education: [{ degree, university, location, graduationYear, gpa }],
  certifications: [{ title, issuingOrganization, year, credentialId, credentialUrl }],
  achievements: [{ title, year, extraInformation }],
  publications: [{ title, publisher, publicationDate, url }],
  caseStudies: [{ title, client, description, outcome, media }],
  licenses: [{ title, licenseNumber, issuingOrganization, issueDate, expiryDate }],
  languages: [{ name, proficiency }],
  interests: [{ name }],
  references: [{ name, position, company, email, phone }],
  mediaGallery: [{ fileName, fileType, description, url }]
}
4. Rendering Logic & Order
Render sections only if valid data exists, in this order:
1.	Hero/About
2.	Skills
3.	Experience
4.	Projects
5.	Education
6.	Certifications
7.	Achievements
8.	Publications
9.	Case Studies
10.	Licenses
11.	Languages
12.	Interests
13.	Media Gallery
14.	References
15.	Contact (always rendered if email or phone present)
Each section is a dedicated JS render function appending content inside a main container. They must skip rendering gracefully if no data or invalid. Utility functions validate individual objects and arrays deeply to avoid empty cards.
Example for projects:
js
function hasProjectData(project) {
  if (!project) return false;
  return Object.values(project).some(field => {
    if (Array.isArray(field)) return field.some(val => val && val.trim && val.trim() !== '');
    return typeof field === 'string' && field.trim() !== '';
  });
}
Only render cards for items with meaningful data.
Avoid Random or Unrelated Images

Do not include any random images or stock photos that are unrelated to the user’s personal data or portfolio content.

All images or media displayed must come only from the user-provided resume.json data or the resumeData localStorage key in the media fields.

The hero section must always display a relevant illustration:

Either the user’s uploaded profile image if available,

Or a carefully selected and purposeful open-license SVG illustration matching the user’s professional title,

No generic or arbitrary images are allowed.

Any additional imagery or gallery items shown must correspond strictly to actual user-provided media.

Avoid placeholders, filler images, or decorative images that do not serve to showcase the user's work or identity.
5. HERO SECTION & ILLUSTRATION
•	Always display a hero illustration:
o	If media URL present in personalInformation, use as a rounded profile hero image with a stylish side illustration (use your creativity in layout).
o	Otherwise, dynamically select and inline an open-license SVG illustration based on the user’s professionalTitle (e.g., developer, designer, engineer).
o	Fallback to a default modern abstract SVG if no matching illustration.
•	The hero headline must display user’s full name and professional title combined.
•	Implement a typewriter or gradient fade-in animation for the hero headline text using pure CSS and JS.
•	Resolve heading visibility: add proper margin-top or spacing to ensure hero heading is never obscured by fixed/sticky nav bar.
6. ANIMATIONS THROUGHOUT SITE
•	All sections and cards animate on entrance with smooth fade and slide (e.g., slide-up + fade-in).
•	Skills section must animate from left to right with staggered delays and combined opacity from 0 to 1 plus horizontal slide (translateX(-30px) to 0).
•	On hover, skill cards, nav links, and projects gently scale up and brighten.
•	Animate hero illustration fade and slide from left; hero text typed or gradient fade.
•	Use CSS will-change for transform and opacity to optimize animations.
Use your own creativity at best , i want you to be creative person ever in the world.
7. SKILLS ICONS AND ANIMATION
Create a horizontally scrolling skills bar using plain JavaScript and CSS where the skill items continuously move from left to right in an infinite loop. The skills should appear as small boxes or tags. Add subtle gradient overlays on the left and right edges of the container to create a fading effect (less opacity) at the edges, so the skills fade out smoothly as they enter/exit the visible area. The scrolling speed should be smooth and continuous, and the entire bar should be responsive.
8. NAVIGATION
•	Sticky header remains visible always.
•	Below 700px viewport width, transform nav into a hamburger toggle menu.
•	Dynamically generate nav from only the sections rendered (max 5 plus Contact).
•	Nav links are smooth-scrolling in-page anchors (href="#section-id").
•	Implement scrollspy: highlight the nav link for visible section on scroll.
•	Ensure nav accessibility: keyboard tabbing, ARIA roles, focus rings.
•	Avoid any external page reload or location changes besides hash navigation.
.   Prevent headings from being hidden behind the fixed/sticky nav bar when navigating via in-page anchor links:

Use the CSS property scroll-margin-top on all anchor targets (sections or headings). This property offsets scroll positioning so the heading appears just below the fixed nav rather than flush at the top (and obscured).

Example CSS snippet to include inline in your style block:

css
section, h1, h2, h3 {
  scroll-margin-top: 70px; /* Adjust this to your nav bar height */
}
9. CONTACT SECTION
•	Present all personal info links as clickable icons (email, phone, LinkedIn, GitHub, portfolio).
•	Glassmorphism style consistent with the rest of the site.
•	Include a contact form with fields: Name, Email (validated), Subject, Message (required).
•	On submit, build a mailto: URL with form data and open default mail client.
•	Disable submit button during send; show a non-blocking toast notification “Message ready to send!”.
•	Accessible form with labels, ARIA attributes, correct tab order.
•	Validate client-side and show user-friendly error messages for invalid input.
10. ALL OTHER SECTIONS
•	Experience: timeline or stacked cards; show company, role, bullets (responsibilities), tools, duration, media.
•	Projects: responsive grid or stack; each card with title, description, technologies, repo/demo links, media.
•	Education, Certifications, Achievements, etc.: uniform card or list layouts; include URLs.
•	Languages/Interests: display with badges or icons.
•	Media Gallery: lazy-loaded responsive image grid with captions.
•	References: only display if filled (name, contact details).
•	Skip empty sections gracefully.
11. RESPONSIVE LAYOUTS
•	Mobile-first approach: vertical stacking, full-width cards on narrow screens.
•	Tablet and desktop get grid/flex layouts with multiple columns; max width container on desktop (e.g., max-width 1024-1200px centered).
•	Breakpoints for media queries:
o	@media (max-width: 600px) for mobile
o	@media (min-width: 701px) for tablets/small desktops
o	@media (min-width: 1024px) for desktop
o	@media (min-width: 1200px) for large screens
•	Typography and spacing scale fluidly.
•	Images use loading="lazy" for performance.
12. PERFORMANCE & ACCESSIBILITY
•	Use semantic HTML5 with appropriate ARIA roles/attributes on all navigation, forms, live regions.
•	Ensure keyboard navigability everywhere.
•	Color contrast tuned for pastel/light text on glass backgrounds.
•	Efficient DOM updates using DocumentFragment for batch insertions.
•	Avoid use of blocking UI like alert().
•	No console errors or warnings.
13. ERROR & LOADING STATES
•	Show inline spinner or skeleton loader on images or asynchronous data fetches.
•	Use try/catch blocks when loading/parsing JSON.
•	Display user-friendly fallback messages when data missing or invalid.
•	Never display raw undefined or null text.
14. FOOTER
•	Always visible, fixed at bottom on larger screens, simple glass style.
•	Contains the text:
made with ❤️ by sitesketch
________________________________________
Hero Section Illustration and Title Details:
•	Always include an illustration closely matched to user’s role:
o	Gather or embed open-license SVGs from sites like unDraw, customizing them inline for best performance.
o	If user has profile media image, present it as a large circular hero photo side-by-side with an abstract or decorative SVG shape to fill whitespace.
o	Title heading uses animated typewriter or smooth gradient fade-in.
•	Add spacing or offset to the top of the heading so it never hides under the fixed or sticky navigation bar. This may be margin or padding with a size matching nav height.
•	The hero’s textual content includes:
o	Full Name in large, bold font,
o	Professional Title immediately below,
o	A summary paragraph if available.
•	Hero area uses a glass panel with subtle blur over a softly colored, layered backdrop.


""".trimIndent()

    }
}