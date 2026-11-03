package com.example.data.model

enum class CompanyCategory(val germanLabel: String) {
    MARKETING_AGENCY("Marketingagentur"),
    WERBEAGENTUR("Werbeagentur"),
    DIGITAL_AGENCY("Digitalagentur"),
    SOCIAL_MEDIA_AGENCY("Social-Media-Agentur"),
    E_COMMERCE("E-Commerce-Unternehmen"),
    MEDIENUNTERNEHMEN("Medienunternehmen"),
    PR_AGENCY("PR- & Kommunikationsagentur"),
    KINDERGARTEN_KITA("Kita & Kindergarten"),
    PRIVATSCHULE("Privatschule"),
    BILDUNGSEINRICHTUNG("Bildungseinrichtung / Berufsschule"),
    TRAEGER_JUGENDHILFE("Träger der Kinder- und Jugendhilfe")
}

data class Company(
    val id: String,
    val name: String,
    val city: String,
    val bundesland: String,
    val category: CompanyCategory,
    val websiteUrl: String,
    val careerUrl: String,
    val placeId: String,
    val discoveredAt: String,
    val verifiedOpportunitiesCount: Int,
    val verificationStatus: VerificationStatus,
    val latitude: Double,
    val longitude: Double
)
