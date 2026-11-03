package com.example.data.model

enum class PopulationTier(val label: String) {
    UNDER_25K("Under 25k (Small towns)"),
    TIER_25K_50K("25k - 50k (Mid-small)"),
    TIER_50K_100K("50k - 100k (Medium)"),
    OVER_100K("Over 100k (Metropolises)")
}

data class GermanCity(
    val id: String,
    val name: String,
    val postalCode: String,
    val bundesland: String,
    val population: Int,
    val populationTier: PopulationTier,
    val isPaused: Boolean = false,
    val isPrioritized: Boolean = false,
    val lastSearchDate: String,
    val discoveredCompaniesCount: Int,
    val verifiedOpportunitiesCount: Int,
    val latitude: Double,
    val longitude: Double
)
