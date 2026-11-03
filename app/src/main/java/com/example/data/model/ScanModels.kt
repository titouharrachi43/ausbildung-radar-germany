package com.example.data.model

data class ScanLog(
    val id: String,
    val timestamp: String,
    val citiesCovered: List<String>,
    val companiesDiscovered: Int,
    val opportunitiesVerified: Int,
    val durationSeconds: Int,
    val status: String,
    val details: String
)

data class AutomationStatus(
    val intervalHours: Int = 5,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val lastScanTime: String = "Today, 04:30 Europe/Berlin",
    val nextScanTime: String = "Today, 09:30 Europe/Berlin",
    val currentBatchCities: List<String> = listOf("Passau", "Bamberg", "Celle", "Görlitz", "Tübingen"),
    val placesApiCallsCount: Int = 412,
    val placesApiDailyLimit: Int = 5000,
    val geminiApiCallsCount: Int = 89,
    val geminiApiDailyLimit: Int = 1500,
    val lockActive: Boolean = false
)

enum class ScanPhase {
    IDLE,
    GOOGLE_PLACES_DISCOVERY,
    CAREER_PAGE_CRAWLER,
    GEMINI_CLASSIFICATION,
    DATABASE_SYNC,
    COMPLETED
}
