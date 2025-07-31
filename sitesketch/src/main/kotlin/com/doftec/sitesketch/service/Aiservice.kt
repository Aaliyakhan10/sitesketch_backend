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
            "model" to "z-ai/glm-4.5-air:free",
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
**Role:** Senior Front-End Developer & UI/UX Specialist  
**Objective:** Create single-file portfolio with glassmorphism design  
**Constraints:** Zero dependencies, iframe-safe, WCAG 2.1 AA compliant  

### 🚀 CORE REQUIREMENTS  
1. **Single HTML File**  
   - Inline CSS/JS only (no external resources)  
   - Max file size: 50KB compressed  
   - Icon CDNs allowed: FontAwesome/Iconify  

2. **Error-Proof Implementation**  
   - Zero console errors in Chrome/Firefox/Safari  
   - Graceful degradation for missing JSON data  
   - `?.` optional chaining for all data access  
   - Try/catch wrapping for localStorage/JSON operations  

3. **Data Handling**  
   ```javascript
   // REQUIRED VALIDATION FUNCTION
   const hasValidContent = (arr) => Array.isArray(arr) && 
     arr.some(item => Object.values(item).some(val => 
       (Array.isArray(val) ? val.join('') : val.toString()
     ).trim().length > 0)
   ```  
   - Priority sources:  
     1. `resume.json` (fetch)  
     2. `localStorage.getItem("resumeData")`  
     3. Fallback: Render minimal UI with error message  

### 🎨 DESIGN SPECIFICATIONS  
**Glassmorphism System:**  
```css
.glass-card {
  background: linear-gradient(135deg, rgba(255,255,255,0.08) 0%, rgba(255,255,255,0.03) 100%);
  backdrop-filter: blur(12px) saturate(160%);
  -webkit-backdrop-filter: blur(12px) saturate(160%);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 33, 0.18);
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
}
```  
**Mandatory Animations:**  
- Staggered fade-in (section appear sequence)  
- Hover scale (cards/buttons)  
- Smooth scroll (anchor navigation)  

### ⚙️ TECHNICAL NON-NEGOTIABLES  
- **Iframe Safety:**  
  - Anchor navigation only (`#section`)  
  - No `window.top` access  
  - Containment: `section { contain: content; }`  

- **Performance:**  
  - Lazy image loading (`loading="lazy"`)  
  - CSS containment for all sections  
  - RequestAnimationFrame for DOM updates  

- **Accessibility:**  
  - Semantic HTML5 tags  
  - Keyboard navigable menu  
  - Reduced motion preference support  
  - ARIA attributes for dynamic content  

### 🔄 RENDER LOGIC  
**Render only if:**  
```mermaid
flowchart LR
  A[personalInformation] -->|Exists| B[Summary]
  B --> C{hasValidContent?}
  C -->|Yes| D[Skills]
  C -->|No| E[Next Section]
  D --> F[Experience]
  F --> G[Projects]
  G --> H[Education]
  H -->|Conditional| I[Other Sections]
```

### ✉️ CONTACT FORM  
**Requirements:**  
```html
<form id="contact" onsubmit="return handleSubmit(event)">
  <!-- Accessibility: aria-label required -->
  <input type="text" name="name" aria-label="Full Name" required>
  <button type="submit" aria-busy="false">Send</button>
</form>
```  
**Validation:**  
- Real-time input validation  
- `mailto:` with encoded subject/body  
- Submit button state management  

### 🧪 TESTING PROTOCOLS  
1. **Data Scenarios:**  
   - Empty JSON  
   - Malformed fields  
   - 500KB+ JSON load  

2. **Browser Tests:**  
   - Mobile (320px) → Desktop (1920px)  
   - Safari 15+, Chrome 110+, Firefox ESR  

3. **Performance Metrics:**  
   - FCP < 1.5s  
   - CLS < 0.1  
   - 60fps animations  

### ✅ DELIVERABLE OUTPUT  
```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <style>
    /* CRITICAL CSS FIRST */
    body { margin: 0; overflow-x: hidden; }
    /* FULL STYLES HERE */
  </style>
</head>
<body>
  <header><!-- Sticky nav --></header>
  
  <main>
    <!-- DYNAMIC CONTENT -->
  </main>

  <script>
    // STRICT MODE REQUIRED
    "use strict";
    
    // DATA LOADER WITH ERROR CONTAINMENT
    async function loadData() {
      try {
        /* ... */
      } catch (err) {
        console.error('Data load failure', err);
        return safeFallback();
      }
    }
    
    // DOM RENDERER WITH VALIDATION
    function renderSection(id, contentFn) {
      const el = document.getElementById(id);
      if(!el) return;
      
      try {
        el.innerHTML = contentFn();
      } catch (renderErr) {
        el.innerHTML = `<p class="error">Content unavailable</p>`;
      }
    }
  </script>
</body>
</html>
```

### 🚫 PROHIBITED  
- External libraries (jQuery, React, etc)  
- `eval()` or `innerHTML` with user input  
- `!important` in CSS  
- Synchronous network requests  

### 💡 PRIORITY ORDER  
1. Zero runtime errors  
2. Iframe compatibility  
3. WCAG 2.1 AA compliance  
4. Glassmorphism aesthetics  
5. Performance metrics  
```

This prompt features:
- Explicit error prevention strategies
- Atomic validation requirements
- Performance budget constraints
- Critical path prioritization
- Visual design specs with code samples
- Machine-readable condition logic
- Security constraints
- Test-driven development requirements

The structure minimizes ambiguity while enforcing production-grade standards through concrete technical guardrails.
""".trimIndent()

    }
}