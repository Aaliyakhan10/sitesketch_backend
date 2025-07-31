package com.doftec.sitesketch.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class Resume(
    val personalInformation: PersonalInformation = PersonalInformation(),
    val summary: String = "",
    val skills: List<Skill> = emptyList(),
    val experience: List<Experience> = emptyList(),
    val education: List<Education> = emptyList(),
    val certifications: List<Certification> = emptyList(),
    val projects: List<Project> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val publications: List<Publication> = emptyList(),
    val caseStudies: List<CaseStudy> = emptyList(),
    val licenses: List<License> = emptyList(),
    val languages: List<Language> = emptyList(),
    val interests: List<Interest> = emptyList(),
    val references: List<Reference> = emptyList(),
    val mediaGallery: List<Media> = emptyList()
)

// --- General Data Classes ---
data class PersonalInformation(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val linkedIn: String = "",
    val gitHub: String = "",
    val portfolio: String = "",
    val website: String = "",
    val media: String = "" // Profile image or intro video
)

data class Skill(val title: String = "", val level: String = "")

data class Experience(
    val jobTitle: String = "",
    val company: String = "",
    val location: String = "",
    val duration: String = "",
    val responsibilities: List<String> = emptyList(),
    val toolsUsed: List<String> = emptyList(),
    val media: String = ""
)

data class Education(
    val degree: String = "",
    val university: String = "",
    val location: String = "",
    val graduationYear: String = "",
    val gpa: String = ""
)

data class Certification(
    val title: String = "",
    val issuingOrganization: String = "",
    val year: String = "",
    val credentialId: String = "",
    val credentialUrl: String = ""
)

data class Project(
    val title: String = "",
    val description: String = "",
    val technologiesUsed: List<String> = emptyList(),
    val githubLink: String = "",
    val liveDemoLink: String = "",
    val media: String = "" // image/video of the project
)

data class Achievement(
    val title: String = "",
    val year: String = "",
    val extraInformation: String = ""
)

data class Language(val id: Int? = null, val name: String = "", val proficiency: String = "")

data class Interest(val id: Int? = null, val name: String = "")

data class Reference(
    val name: String = "",
    val position: String = "",
    val company: String = "",
    val email: String = "",
    val phone: String = ""
)

data class Media(
    val fileName: String = "",
    val fileType: String = "", // e.g. "image", "video", "pdf"
    val description: String = "",
    val url: String = ""
)

// --- Profession-Specific Optional Add-ons ---
data class Publication(
    val title: String = "",
    val publisher: String = "",
    val publicationDate: String = "",
    val url: String = ""
)

data class CaseStudy(
    val title: String = "",
    val client: String = "",
    val description: String = "",
    val outcome: String = "",
    val media: String = ""
)

data class License(
    val title: String = "",
    val licenseNumber: String = "",
    val issuingOrganization: String = "",
    val issueDate: String = "",
    val expiryDate: String = ""
)
