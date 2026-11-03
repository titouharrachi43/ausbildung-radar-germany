package com.example.data.model

enum class AusbildungCategory(val displayName: String) {
    MARKETING("Marketing & Media"),
    EDUCATION("Education & Childcare")
}

enum class TrainingType(val germanLabel: String) {
    DUALE_AUSBILDUNG("Duale Ausbildung (Betrieb + Berufsschule)"),
    SCHULISCHE_AUSBILDUNG("Schulische Ausbildung"),
    PIA("PiA (Praxisintegrierte Ausbildung)")
}

enum class VerificationStatus(val label: String) {
    CONFIRMED_ACTIVE("Confirmed Active"),
    FUTURE_OPPORTUNITY("Future Opportunity"),
    POTENTIAL_EMPLOYER("Potential Employer"),
    EXPIRED("Expired"),
    UNVERIFIED("Unverified")
}

enum class ApplicationStatus(val label: String) {
    NOT_APPLIED("Not Applied"),
    INTERESTED("Interested"),
    TO_APPLY("To Apply"),
    APPLIED("Application Sent"),
    INTERVIEW("Interview"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    NO_RESPONSE("No Response")
}

data class Opportunity(
    val id: String,
    val title: String, // Original German job title
    val englishSummary: String, // Short English translation / explanation
    val companyName: String,
    val city: String,
    val postalCode: String,
    val bundesland: String,
    val category: AusbildungCategory,
    val subcategory: String,
    val trainingType: TrainingType,
    val startYear: Int, // 2027 or 2028
    val startDate: String,
    val applicationDeadline: String,
    val salary: String, // e.g. "1.150 € / Monat (1. Jahr)"
    val languageRequirement: String, // e.g. "B2 Deutsch", "C1 Deutsch"
    val qualifications: String,
    val description: String,
    val applicationUrl: String,
    val companyWebsite: String,
    val sourceUrl: String,
    val verificationStatus: VerificationStatus,
    val firstDiscoveredAt: String,
    val lastVerifiedAt: String,
    val isSaved: Boolean = false,
    val applicationStatus: ApplicationStatus = ApplicationStatus.NOT_APPLIED,
    val latitude: Double = 51.1657,
    val longitude: Double = 10.4515,
    val isDemoData: Boolean = true
)
