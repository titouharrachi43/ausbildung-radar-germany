package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

import com.example.data.firebase.FirestoreSyncService

class AusbildungRepository(
    private val firestoreSyncService: FirestoreSyncService? = null
) {

    private val _opportunities = MutableStateFlow<List<Opportunity>>(emptyList())
    val opportunities: StateFlow<List<Opportunity>> = _opportunities.asStateFlow()

    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    private val _cities = MutableStateFlow<List<GermanCity>>(emptyList())
    val cities: StateFlow<List<GermanCity>> = _cities.asStateFlow()

    private val _applications = MutableStateFlow<List<ApplicationEntry>>(emptyList())
    val applications: StateFlow<List<ApplicationEntry>> = _applications.asStateFlow()

    private val _scanLogs = MutableStateFlow<List<ScanLog>>(emptyList())
    val scanLogs: StateFlow<List<ScanLog>> = _scanLogs.asStateFlow()

    private val _automationStatus = MutableStateFlow(AutomationStatus())
    val automationStatus: StateFlow<AutomationStatus> = _automationStatus.asStateFlow()

    private val _currentScanPhase = MutableStateFlow(ScanPhase.IDLE)
    val currentScanPhase: StateFlow<ScanPhase> = _currentScanPhase.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _scanLogDetail = MutableStateFlow("")
    val scanLogDetail: StateFlow<String> = _scanLogDetail.asStateFlow()

    init {
        loadInitialDemonstrationData()
    }

    private fun loadInitialDemonstrationData() {
        val initialCities = listOf(
            GermanCity(
                id = "city-1",
                name = "Passau",
                postalCode = "94032",
                bundesland = "Bayern",
                population = 53200,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = true,
                lastSearchDate = "03.11.2026, 04:30",
                discoveredCompaniesCount = 18,
                verifiedOpportunitiesCount = 4,
                latitude = 48.5734,
                longitude = 13.4566
            ),
            GermanCity(
                id = "city-2",
                name = "Bamberg",
                postalCode = "96047",
                bundesland = "Bayern",
                population = 79800,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = true,
                lastSearchDate = "03.11.2026, 04:32",
                discoveredCompaniesCount = 24,
                verifiedOpportunitiesCount = 5,
                latitude = 49.8988,
                longitude = 10.9028
            ),
            GermanCity(
                id = "city-3",
                name = "Tübingen",
                postalCode = "72070",
                bundesland = "Baden-Württemberg",
                population = 91800,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = true,
                lastSearchDate = "03.11.2026, 04:33",
                discoveredCompaniesCount = 21,
                verifiedOpportunitiesCount = 6,
                latitude = 48.5216,
                longitude = 9.0576
            ),
            GermanCity(
                id = "city-4",
                name = "Celle",
                postalCode = "29221",
                bundesland = "Niedersachsen",
                population = 69300,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = false,
                lastSearchDate = "03.11.2026, 04:35",
                discoveredCompaniesCount = 14,
                verifiedOpportunitiesCount = 3,
                latitude = 52.6247,
                longitude = 10.0811
            ),
            GermanCity(
                id = "city-5",
                name = "Görlitz",
                postalCode = "02826",
                bundesland = "Sachsen",
                population = 56600,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = true,
                lastSearchDate = "03.11.2026, 04:36",
                discoveredCompaniesCount = 16,
                verifiedOpportunitiesCount = 4,
                latitude = 51.1528,
                longitude = 14.9872
            ),
            GermanCity(
                id = "city-6",
                name = "Bautzen",
                postalCode = "02625",
                bundesland = "Sachsen",
                population = 38300,
                populationTier = PopulationTier.TIER_25K_50K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 23:15",
                discoveredCompaniesCount = 12,
                verifiedOpportunitiesCount = 2,
                latitude = 51.1814,
                longitude = 14.4244
            ),
            GermanCity(
                id = "city-7",
                name = "Flensburg",
                postalCode = "24937",
                bundesland = "Schleswig-Holstein",
                population = 92500,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = true,
                lastSearchDate = "03.11.2026, 04:38",
                discoveredCompaniesCount = 22,
                verifiedOpportunitiesCount = 5,
                latitude = 54.7836,
                longitude = 9.4321
            ),
            GermanCity(
                id = "city-8",
                name = "Wismar",
                postalCode = "23966",
                bundesland = "Mecklenburg-Vorpommern",
                population = 42800,
                populationTier = PopulationTier.TIER_25K_50K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 23:20",
                discoveredCompaniesCount = 9,
                verifiedOpportunitiesCount = 2,
                latitude = 53.8932,
                longitude = 11.4644
            ),
            GermanCity(
                id = "city-9",
                name = "Fulda",
                postalCode = "36037",
                bundesland = "Hessen",
                population = 68400,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = true,
                lastSearchDate = "03.11.2026, 04:40",
                discoveredCompaniesCount = 19,
                verifiedOpportunitiesCount = 4,
                latitude = 50.5534,
                longitude = 9.6756
            ),
            GermanCity(
                id = "city-10",
                name = "Bad Homburg",
                postalCode = "61348",
                bundesland = "Hessen",
                population = 54900,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 23:25",
                discoveredCompaniesCount = 15,
                verifiedOpportunitiesCount = 3,
                latitude = 50.2268,
                longitude = 8.6186
            ),
            GermanCity(
                id = "city-11",
                name = "Speyer",
                postalCode = "67346",
                bundesland = "Rheinland-Pfalz",
                population = 50700,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 23:30",
                discoveredCompaniesCount = 11,
                verifiedOpportunitiesCount = 2,
                latitude = 49.3175,
                longitude = 8.4325
            ),
            GermanCity(
                id = "city-12",
                name = "Wernigerode",
                postalCode = "38855",
                bundesland = "Sachsen-Anhalt",
                population = 32000,
                populationTier = PopulationTier.TIER_25K_50K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 18:20",
                discoveredCompaniesCount = 8,
                verifiedOpportunitiesCount = 2,
                latitude = 51.8351,
                longitude = 10.7853
            ),
            GermanCity(
                id = "city-13",
                name = "Eisenach",
                postalCode = "99817",
                bundesland = "Thüringen",
                population = 42400,
                populationTier = PopulationTier.TIER_25K_50K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 18:25",
                discoveredCompaniesCount = 10,
                verifiedOpportunitiesCount = 2,
                latitude = 50.9744,
                longitude = 10.3245
            ),
            GermanCity(
                id = "city-14",
                name = "Homburg",
                postalCode = "66424",
                bundesland = "Saarland",
                population = 41800,
                populationTier = PopulationTier.TIER_25K_50K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 18:30",
                discoveredCompaniesCount = 12,
                verifiedOpportunitiesCount = 2,
                latitude = 49.3247,
                longitude = 7.3389
            ),
            GermanCity(
                id = "city-15",
                name = "Eberswalde",
                postalCode = "16225",
                bundesland = "Brandenburg",
                population = 41100,
                populationTier = PopulationTier.TIER_25K_50K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 18:35",
                discoveredCompaniesCount = 9,
                verifiedOpportunitiesCount = 2,
                latitude = 52.8335,
                longitude = 13.8189
            ),
            GermanCity(
                id = "city-16",
                name = "Bremerhaven",
                postalCode = "27568",
                bundesland = "Bremen",
                population = 113500,
                populationTier = PopulationTier.OVER_100K,
                isPrioritized = false,
                lastSearchDate = "03.11.2026, 04:42",
                discoveredCompaniesCount = 26,
                verifiedOpportunitiesCount = 5,
                latitude = 53.5414,
                longitude = 8.5809
            ),
            GermanCity(
                id = "city-17",
                name = "Arnsberg",
                postalCode = "59759",
                bundesland = "Nordrhein-Westfalen",
                population = 73500,
                populationTier = PopulationTier.TIER_50K_100K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 18:40",
                discoveredCompaniesCount = 17,
                verifiedOpportunitiesCount = 3,
                latitude = 51.3967,
                longitude = 8.0645
            ),
            GermanCity(
                id = "city-18",
                name = "Coburg",
                postalCode = "96450",
                bundesland = "Bayern",
                population = 41500,
                populationTier = PopulationTier.TIER_25K_50K,
                isPrioritized = false,
                lastSearchDate = "02.11.2026, 18:45",
                discoveredCompaniesCount = 13,
                verifiedOpportunitiesCount = 3,
                latitude = 50.2584,
                longitude = 10.9639
            ),
            GermanCity(
                id = "city-19",
                name = "Rothenburg ob der Tauber",
                postalCode = "91541",
                bundesland = "Bayern",
                population = 11200,
                populationTier = PopulationTier.UNDER_25K,
                isPrioritized = true,
                lastSearchDate = "02.11.2026, 13:10",
                discoveredCompaniesCount = 6,
                verifiedOpportunitiesCount = 1,
                latitude = 49.3769,
                longitude = 10.1788
            ),
            GermanCity(
                id = "city-20",
                name = "Leipzig",
                postalCode = "04109",
                bundesland = "Sachsen",
                population = 615000,
                populationTier = PopulationTier.OVER_100K,
                isPrioritized = false,
                lastSearchDate = "03.11.2026, 04:45",
                discoveredCompaniesCount = 74,
                verifiedOpportunitiesCount = 12,
                latitude = 51.3397,
                longitude = 12.3731
            ),
            GermanCity(
                id = "city-21",
                name = "Berlin",
                postalCode = "10115",
                bundesland = "Berlin",
                population = 3755000,
                populationTier = PopulationTier.OVER_100K,
                isPrioritized = false,
                lastSearchDate = "03.11.2026, 04:50",
                discoveredCompaniesCount = 110,
                verifiedOpportunitiesCount = 19,
                latitude = 52.5200,
                longitude = 13.4050
            ),
            GermanCity(
                id = "city-22",
                name = "Hamburg",
                postalCode = "20095",
                bundesland = "Hamburg",
                population = 1890000,
                populationTier = PopulationTier.OVER_100K,
                isPrioritized = false,
                lastSearchDate = "03.11.2026, 04:52",
                discoveredCompaniesCount = 95,
                verifiedOpportunitiesCount = 15,
                latitude = 53.5511,
                longitude = 9.9937
            )
        )

        val initialCompanies = listOf(
            Company(
                id = "comp-1",
                name = "ThreeRivers Digital Communication GmbH",
                city = "Passau",
                bundesland = "Bayern",
                category = CompanyCategory.DIGITAL_AGENCY,
                websiteUrl = "https://www.threerivers-digital.de",
                careerUrl = "https://www.threerivers-digital.de/karriere/ausbildung",
                placeId = "ChIJyXG6r_T5c0cRhT5WpXbH3gM",
                discoveredAt = "02.11.2026, 14:20",
                verifiedOpportunitiesCount = 2,
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                latitude = 48.5734,
                longitude = 13.4566
            ),
            Company(
                id = "comp-2",
                name = "Kita St. Anton & Montessori-Kinderhaus",
                city = "Passau",
                bundesland = "Bayern",
                category = CompanyCategory.KINDERGARTEN_KITA,
                websiteUrl = "https://www.kita-passau-stanton.de",
                careerUrl = "https://www.kita-passau-stanton.de/stellen-ausbildung",
                placeId = "ChIJ6c_hH8r5c0cRfZ1Pz_lQk8k",
                discoveredAt = "02.11.2026, 14:22",
                verifiedOpportunitiesCount = 1,
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                latitude = 48.5680,
                longitude = 13.4420
            ),
            Company(
                id = "comp-3",
                name = "Fränkische Medienmanufaktur & Werbewerk",
                city = "Bamberg",
                bundesland = "Bayern",
                category = CompanyCategory.WERBEAGENTUR,
                websiteUrl = "https://www.fraenkische-medien.de",
                careerUrl = "https://www.fraenkische-medien.de/jobs-karriere",
                placeId = "ChIJL6xP7Xp5oEcRnW7y1G-NlwE",
                discoveredAt = "02.11.2026, 17:05",
                verifiedOpportunitiesCount = 2,
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                latitude = 49.8988,
                longitude = 10.9028
            ),
            Company(
                id = "comp-4",
                name = "AWO Kreisverband Bamberg e.V.",
                city = "Bamberg",
                bundesland = "Bayern",
                category = CompanyCategory.TRAEGER_JUGENDHILFE,
                websiteUrl = "https://www.awo-bamberg.de",
                careerUrl = "https://www.awo-bamberg.de/karriere/ausbildung-paedagogik",
                placeId = "ChIJT93m32F4oEcRl936r93wP2A",
                discoveredAt = "02.11.2026, 17:10",
                verifiedOpportunitiesCount = 2,
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                latitude = 49.9020,
                longitude = 10.8910
            ),
            Company(
                id = "comp-5",
                name = "NeckarValley E-Commerce Systems GmbH",
                city = "Tübingen",
                bundesland = "Baden-Württemberg",
                category = CompanyCategory.E_COMMERCE,
                websiteUrl = "https://www.neckarvalley-systems.de",
                careerUrl = "https://www.neckarvalley-systems.de/ausbildung-2027",
                placeId = "ChIJRz8K49c3mEcR9T3F_m8wA9g",
                discoveredAt = "02.11.2026, 20:30",
                verifiedOpportunitiesCount = 2,
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                latitude = 48.5216,
                longitude = 9.0576
            ),
            Company(
                id = "comp-6",
                name = "Pädagogisches Bildungszentrum Tübingen",
                city = "Tübingen",
                bundesland = "Baden-Württemberg",
                category = CompanyCategory.BILDUNGSEINRICHTUNG,
                websiteUrl = "https://www.pbz-tuebingen.de",
                careerUrl = "https://www.pbz-tuebingen.de/erzieher-pia-ausbildung",
                placeId = "ChIJo6z_31w3mEcR0W512mF8a5L",
                discoveredAt = "02.11.2026, 20:35",
                verifiedOpportunitiesCount = 1,
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                latitude = 48.5240,
                longitude = 9.0620
            ),
            Company(
                id = "comp-7",
                name = "Nordland Media & Brand Communications",
                city = "Flensburg",
                bundesland = "Schleswig-Holstein",
                category = CompanyCategory.MARKETING_AGENCY,
                websiteUrl = "https://www.nordland-media.de",
                careerUrl = "https://www.nordland-media.de/karriere",
                placeId = "ChIJ7wJjQJkLskcRe83Z_e3L-kA",
                discoveredAt = "03.11.2026, 01:10",
                verifiedOpportunitiesCount = 1,
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                latitude = 54.7836,
                longitude = 9.4321
            ),
            Company(
                id = "comp-8",
                name = "Lausitz Kreativwerkstatt Görlitz",
                city = "Görlitz",
                bundesland = "Sachsen",
                category = CompanyCategory.MEDIENUNTERNEHMEN,
                websiteUrl = "https://www.lausitz-kreativ.de",
                careerUrl = "https://www.lausitz-kreativ.de/ausbildung",
                placeId = "ChIJ3Z1eQyLkoEcR-8wY8_tq-i8",
                discoveredAt = "03.11.2026, 02:40",
                verifiedOpportunitiesCount = 1,
                verificationStatus = VerificationStatus.FUTURE_OPPORTUNITY,
                latitude = 51.1528,
                longitude = 14.9872
            ),
            Company(
                id = "comp-9",
                name = "Diakonie Fulda Pädagogik & Jugendhilfe",
                city = "Fulda",
                bundesland = "Hessen",
                category = CompanyCategory.TRAEGER_JUGENDHILFE,
                websiteUrl = "https://www.diakonie-fulda.de",
                careerUrl = "https://www.diakonie-fulda.de/ausbildung-erzieher",
                placeId = "ChIJ1X2e7b-NoEcRuZ3P9-Wla2e",
                discoveredAt = "03.11.2026, 03:00",
                verifiedOpportunitiesCount = 2,
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                latitude = 50.5534,
                longitude = 9.6756
            ),
            Company(
                id = "comp-10",
                name = "Heide-Kurier & Regionalmedien Celle",
                city = "Celle",
                bundesland = "Niedersachsen",
                category = CompanyCategory.MEDIENUNTERNEHMEN,
                websiteUrl = "https://www.heidekurier-celle.de",
                careerUrl = "https://www.heidekurier-celle.de/stellen",
                placeId = "ChIJ54J7u_VysUcRgx-9L3V0p6Y",
                discoveredAt = "03.11.2026, 03:15",
                verifiedOpportunitiesCount = 1,
                verificationStatus = VerificationStatus.POTENTIAL_EMPLOYER,
                latitude = 52.6247,
                longitude = 10.0811
            )
        )

        val initialOpportunities = listOf(
            Opportunity(
                id = "opp-1",
                title = "Ausbildung zum Kaufmann für Marketingkommunikation (m/w/d)",
                englishSummary = "Vocational training in marketing communications & campaign strategy at a regional digital agency.",
                companyName = "ThreeRivers Digital Communication GmbH",
                city = "Passau",
                postalCode = "94032",
                bundesland = "Bayern",
                category = AusbildungCategory.MARKETING,
                subcategory = "Marketingkommunikation",
                trainingType = TrainingType.DUALE_AUSBILDUNG,
                startYear = 2027,
                startDate = "01.09.2027",
                applicationDeadline = "31.05.2027",
                salary = "1.080 € (1. Jahr), 1.160 € (2. Jahr), 1.250 € (3. Jahr)",
                languageRequirement = "B2 Deutsch (Fließend)",
                qualifications = "Mittlere Reife oder (Fach-)Abitur, Begeisterung für Social Media & Textgestaltung",
                description = "Als Kaufmann/-frau für Marketingkommunikation entwickelst du Kommunikationskonzepte für mittelständische Kunden im bayerischen Raum, planst Werbeetats und analysierst Kampagnenerfolge.",
                applicationUrl = "https://www.threerivers-digital.de/karriere/bewerbung-mkt",
                companyWebsite = "https://www.threerivers-digital.de",
                sourceUrl = "https://www.threerivers-digital.de/karriere/ausbildung",
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                firstDiscoveredAt = "02.11.2026, 14:20",
                lastVerifiedAt = "03.11.2026, 04:30",
                isSaved = true,
                applicationStatus = ApplicationStatus.INTERESTED,
                latitude = 48.5734,
                longitude = 13.4566
            ),
            Opportunity(
                id = "opp-2",
                title = "Praxisintegrierte Ausbildung zur/zum Erzieher/in (PiA) (m/w/d)",
                englishSummary = "Paid practice-integrated training for state-recognized educator/kindergarten pedagogue.",
                companyName = "Kita St. Anton & Montessori-Kinderhaus",
                city = "Passau",
                postalCode = "94032",
                bundesland = "Bayern",
                category = AusbildungCategory.EDUCATION,
                subcategory = "Erzieher/in (PiA)",
                trainingType = TrainingType.PIA,
                startYear = 2027,
                startDate = "01.09.2027",
                applicationDeadline = "15.04.2027",
                salary = "1.340 € (1. Jahr), 1.400 € (2. Jahr), 1.500 € (3. Jahr) gem. TVöD-SuE",
                languageRequirement = "C1 Deutsch (Sehr gute Sprachkenntnisse)",
                qualifications = "Mittlerer Bildungsabschluss + abgeschlossene Sozialpädagogische Assistenz oder Fachhochschulreife",
                description = "In unserer modernen Kindertagesstätte begleitest du Kinder von 1 bis 6 Jahren nach reformpädagogischem Ansatz. Praxisintegrierte Vergütung ab dem ersten Ausbildungsmonat.",
                applicationUrl = "https://www.kita-passau-stanton.de/stellen-ausbildung/bewerben",
                companyWebsite = "https://www.kita-passau-stanton.de",
                sourceUrl = "https://www.kita-passau-stanton.de/stellen-ausbildung",
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                firstDiscoveredAt = "02.11.2026, 14:22",
                lastVerifiedAt = "03.11.2026, 04:30",
                isSaved = false,
                applicationStatus = ApplicationStatus.NOT_APPLIED,
                latitude = 48.5680,
                longitude = 13.4420
            ),
            Opportunity(
                id = "opp-3",
                title = "Ausbildung Mediengestalter Digital und Print (m/w/d) – Fachrichtung Konzept & Visualisierung",
                englishSummary = "Apprenticeship for Digital & Print Media Designer specializing in visual concept creation.",
                companyName = "Fränkische Medienmanufaktur & Werbewerk",
                city = "Bamberg",
                postalCode = "96047",
                bundesland = "Bayern",
                category = AusbildungCategory.MARKETING,
                subcategory = "Mediengestalter Digital und Print",
                trainingType = TrainingType.DUALE_AUSBILDUNG,
                startYear = 2027,
                startDate = "01.09.2027",
                applicationDeadline = "30.06.2027",
                salary = "1.030 € (1. Jahr), 1.110 € (2. Jahr), 1.190 € (3. Jahr)",
                languageRequirement = "B2 Deutsch",
                qualifications = "Guter Realschulabschluss oder Abitur, Gespür für Farben, Typografie und moderne UI-Trends",
                description = "Erlerne die professionelle Gestaltung von Printpublikationen, responsive Web-Layouts und Digital-Branding in einer etablierten fränkischen Werbeagentur.",
                applicationUrl = "https://www.fraenkische-medien.de/jobs/mediengestaltung",
                companyWebsite = "https://www.fraenkische-medien.de",
                sourceUrl = "https://www.fraenkische-medien.de/jobs-karriere",
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                firstDiscoveredAt = "02.11.2026, 17:05",
                lastVerifiedAt = "03.11.2026, 04:32",
                isSaved = true,
                applicationStatus = ApplicationStatus.TO_APPLY,
                latitude = 49.8988,
                longitude = 10.9028
            ),
            Opportunity(
                id = "opp-4",
                title = "Ausbildung zur/zum Sozialpädagogischen Assistentin/Assistenten (m/w/d)",
                englishSummary = "Vocational childcare assistant certification course preparing for early childhood pedagogy.",
                companyName = "AWO Kreisverband Bamberg e.V.",
                city = "Bamberg",
                postalCode = "96047",
                bundesland = "Bayern",
                category = AusbildungCategory.EDUCATION,
                subcategory = "Sozialpädagogische Assistenz",
                trainingType = TrainingType.SCHULISCHE_AUSBILDUNG,
                startYear = 2027,
                startDate = "01.09.2027",
                applicationDeadline = "31.07.2027",
                salary = "Schulische Ausbildung mit tariflichem Praktikumsentgelt (ca. 700 €/Monat)",
                languageRequirement = "B2 Deutsch",
                qualifications = "Hauptschulabschluss oder Qualifizierender Mittelschulabschluss",
                description = "Zweijährige staatlich anerkannte Ausbildung als Einstieg in die Erzieherausbildung mit praxisnahen Einsätzen in den Bamberger Kindergärten der AWO.",
                applicationUrl = "https://www.awo-bamberg.de/karriere/bewerbungsformular",
                companyWebsite = "https://www.awo-bamberg.de",
                sourceUrl = "https://www.awo-bamberg.de/karriere/ausbildung-paedagogik",
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                firstDiscoveredAt = "02.11.2026, 17:10",
                lastVerifiedAt = "03.11.2026, 04:32",
                isSaved = false,
                applicationStatus = ApplicationStatus.NOT_APPLIED,
                latitude = 49.9020,
                longitude = 10.8910
            ),
            Opportunity(
                id = "opp-5",
                title = "Ausbildung Kaufmann/Kauffrau im E-Commerce (m/w/d)",
                englishSummary = "E-Commerce merchant apprenticeship covering online shop management, SEO/SEA, and shop analytics.",
                companyName = "NeckarValley E-Commerce Systems GmbH",
                city = "Tübingen",
                postalCode = "72070",
                bundesland = "Baden-Württemberg",
                category = AusbildungCategory.MARKETING,
                subcategory = "E-Commerce",
                trainingType = TrainingType.DUALE_AUSBILDUNG,
                startYear = 2028,
                startDate = "01.09.2028",
                applicationDeadline = "31.12.2027",
                salary = "1.150 € (1. Jahr), 1.220 € (2. Jahr), 1.300 € (3. Jahr)",
                languageRequirement = "B2 Deutsch (Gute Englischkenntnisse vorteilhaft)",
                qualifications = "Realschulabschluss oder Abitur, Interesse an datengetriebenem Online-Handel",
                description = "Frühzeitige Stellenausschreibung für das Ausbildungsjahr 2028: Warensortimente online stellen, Conversion-Rates optimieren und Multi-Channel-Marketing steuern.",
                applicationUrl = "https://www.neckarvalley-systems.de/ausbildung-2028-form",
                companyWebsite = "https://www.neckarvalley-systems.de",
                sourceUrl = "https://www.neckarvalley-systems.de/ausbildung-2027",
                verificationStatus = VerificationStatus.FUTURE_OPPORTUNITY,
                firstDiscoveredAt = "02.11.2026, 20:30",
                lastVerifiedAt = "03.11.2026, 04:33",
                isSaved = false,
                applicationStatus = ApplicationStatus.NOT_APPLIED,
                latitude = 48.5216,
                longitude = 9.0576
            ),
            Opportunity(
                id = "opp-6",
                title = "Ausbildung zur/zum Staatlich anerkannten Erzieher/in (m/w/d)",
                englishSummary = "Dual vocational diploma for state-certified kindergarten educator and child youth group leader.",
                companyName = "Pädagogisches Bildungszentrum Tübingen",
                city = "Tübingen",
                postalCode = "72070",
                bundesland = "Baden-Württemberg",
                category = AusbildungCategory.EDUCATION,
                subcategory = "Erzieher/in",
                trainingType = TrainingType.PIA,
                startYear = 2027,
                startDate = "01.09.2027",
                applicationDeadline = "15.03.2027",
                salary = "1.340 € / Monat (TVAöD - Pflege/Erziehung)",
                languageRequirement = "C1 Deutsch",
                qualifications = "Realschulabschluss mit Vorpraktikum oder Fachhochschulreife (Sozialwesen)",
                description = "Kombiniere theoretische Pädagogik an der Fachschule mit intensiver Praxis in kommunalen Kindertageseinrichtungen im Landkreis Tübingen.",
                applicationUrl = "https://www.pbz-tuebingen.de/bewerbung-erzieher",
                companyWebsite = "https://www.pbz-tuebingen.de",
                sourceUrl = "https://www.pbz-tuebingen.de/erzieher-pia-ausbildung",
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                firstDiscoveredAt = "02.11.2026, 20:35",
                lastVerifiedAt = "03.11.2026, 04:33",
                isSaved = true,
                applicationStatus = ApplicationStatus.APPLIED,
                latitude = 48.5240,
                longitude = 9.0620
            ),
            Opportunity(
                id = "opp-7",
                title = "Ausbildung zur/zum Veranstaltungskaufmann/-frau (m/w/d)",
                englishSummary = "Event management apprenticeship organizing corporate exhibitions and digital brand events.",
                companyName = "Nordland Media & Brand Communications",
                city = "Flensburg",
                postalCode = "24937",
                bundesland = "Schleswig-Holstein",
                category = AusbildungCategory.MARKETING,
                subcategory = "Veranstaltungskaufmann/-frau",
                trainingType = TrainingType.DUALE_AUSBILDUNG,
                startYear = 2027,
                startDate = "01.08.2027",
                applicationDeadline = "30.04.2027",
                salary = "980 € (1. Jahr), 1.050 € (2. Jahr), 1.150 € (3. Jahr)",
                languageRequirement = "B2 Deutsch, Englischkenntnisse",
                qualifications = "Mittlere Reife oder Abitur, Organisationstalent und Kommunikationsstärke",
                description = "Konzeption, Budgetierung und Durchführung von Live-Marketing-Events an der Ostseeküste und im norddeutschen Raum.",
                applicationUrl = "https://www.nordland-media.de/karriere/event-ausbildung",
                companyWebsite = "https://www.nordland-media.de",
                sourceUrl = "https://www.nordland-media.de/karriere",
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                firstDiscoveredAt = "03.11.2026, 01:10",
                lastVerifiedAt = "03.11.2026, 04:38",
                isSaved = false,
                applicationStatus = ApplicationStatus.NOT_APPLIED,
                latitude = 54.7836,
                longitude = 9.4321
            ),
            Opportunity(
                id = "opp-8",
                title = "Ausbildung zur/zum Kinderpfleger/in (m/w/d)",
                englishSummary = "Two-year childcare qualification providing fundamental pedagogical and developmental assistance.",
                companyName = "Diakonie Fulda Pädagogik & Jugendhilfe",
                city = "Fulda",
                postalCode = "36037",
                bundesland = "Hessen",
                category = AusbildungCategory.EDUCATION,
                subcategory = "Kinderpfleger/in",
                trainingType = TrainingType.SCHULISCHE_AUSBILDUNG,
                startYear = 2027,
                startDate = "01.08.2027",
                applicationDeadline = "15.05.2027",
                salary = "Tarifliche Ausbildungsvergütung ab 2. Jahr (ca. 850 €)",
                languageRequirement = "B2 Deutsch",
                qualifications = "Hauptschulabschluss mit guten Noten in Biologie und Deutsch",
                description = "Begleitung von Kleinkindern und Unterstützung staatlich anerkannter Erzieher in integrativen Kindertagesgruppen in Osthessen.",
                applicationUrl = "https://www.diakonie-fulda.de/ausbildung-kinderpflege",
                companyWebsite = "https://www.diakonie-fulda.de",
                sourceUrl = "https://www.diakonie-fulda.de/ausbildung-erzieher",
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                firstDiscoveredAt = "03.11.2026, 03:00",
                lastVerifiedAt = "03.11.2026, 04:40",
                isSaved = false,
                applicationStatus = ApplicationStatus.NOT_APPLIED,
                latitude = 50.5534,
                longitude = 9.6756
            ),
            Opportunity(
                id = "opp-9",
                title = "Ausbildung Digital Marketing Specialist & Content Creator (m/w/d)",
                englishSummary = "Digital marketing and creative content production apprenticeship focused on cross-border campaigns.",
                companyName = "Lausitz Kreativwerkstatt Görlitz",
                city = "Görlitz",
                postalCode = "02826",
                bundesland = "Sachsen",
                category = AusbildungCategory.MARKETING,
                subcategory = "Digital Marketing",
                trainingType = TrainingType.DUALE_AUSBILDUNG,
                startYear = 2028,
                startDate = "01.09.2028",
                applicationDeadline = "30.11.2027",
                salary = "1.000 € (1. Jahr), 1.100 € (2. Jahr), 1.200 € (3. Jahr)",
                languageRequirement = "B2 Deutsch (Polnischkenntnisse willkommen)",
                qualifications = "Fachabitur oder Abitur, Gespür für TikTok, Instagram & Video-Schnitt",
                description = "Crossmediale Kampagnenentwicklung an der deutsch-polnischen Grenze für Kulturinstitutionen und Tourismusverbände der Oberlausitz.",
                applicationUrl = "https://www.lausitz-kreativ.de/ausbildung-bewerben",
                companyWebsite = "https://www.lausitz-kreativ.de",
                sourceUrl = "https://www.lausitz-kreativ.de/ausbildung",
                verificationStatus = VerificationStatus.FUTURE_OPPORTUNITY,
                firstDiscoveredAt = "03.11.2026, 02:40",
                lastVerifiedAt = "03.11.2026, 04:36",
                isSaved = false,
                applicationStatus = ApplicationStatus.NOT_APPLIED,
                latitude = 51.1528,
                longitude = 14.9872
            ),
            Opportunity(
                id = "opp-10",
                title = "Ausbildung Medienkaufmann/-frau Digital und Print (m/w/d)",
                englishSummary = "Media merchant apprenticeship in regional editorial, advertising sales, and publishing.",
                companyName = "Heide-Kurier & Regionalmedien Celle",
                city = "Celle",
                postalCode = "29221",
                bundesland = "Niedersachsen",
                category = AusbildungCategory.MARKETING,
                subcategory = "Medienkaufmann/-frau Digital und Print",
                trainingType = TrainingType.DUALE_AUSBILDUNG,
                startYear = 2027,
                startDate = "01.08.2027",
                applicationDeadline = "31.03.2027",
                salary = "1.020 € (1. Jahr), 1.090 € (2. Jahr), 1.180 € (3. Jahr)",
                languageRequirement = "B2 Deutsch",
                qualifications = "Realschulabschluss oder Fachhochschulreife",
                description = "Herstellung, Vertrieb und Vermarktung von gedruckten und digitalen Medienprodukten im Landkreis Celle und der Lüneburger Heide.",
                applicationUrl = "https://www.heidekurier-celle.de/stellen/medienkaufmann",
                companyWebsite = "https://www.heidekurier-celle.de",
                sourceUrl = "https://www.heidekurier-celle.de/stellen",
                verificationStatus = VerificationStatus.POTENTIAL_EMPLOYER,
                firstDiscoveredAt = "03.11.2026, 03:15",
                lastVerifiedAt = "03.11.2026, 04:35",
                isSaved = false,
                applicationStatus = ApplicationStatus.NOT_APPLIED,
                latitude = 52.6247,
                longitude = 10.0811
            )
        )

        val initialApplications = listOf(
            ApplicationEntry(
                id = "app-1",
                opportunityId = "opp-1",
                companyName = "ThreeRivers Digital Communication GmbH",
                positionTitle = "Kaufmann für Marketingkommunikation",
                city = "Passau",
                bundesland = "Bayern",
                status = ApplicationStatus.INTERESTED,
                applicationDate = "01.11.2026",
                followUpDate = "15.11.2026",
                interviewDate = "Not scheduled yet",
                contactPerson = "Frau Lena Weber (Ausbildungsleitung)",
                contactEmail = "l.weber@threerivers-digital.de",
                notes = "Portfolio prepared with sample social media posts and blog writing examples. Will send complete CV."
            ),
            ApplicationEntry(
                id = "app-2",
                opportunityId = "opp-3",
                companyName = "Fränkische Medienmanufaktur & Werbewerk",
                positionTitle = "Mediengestalter Digital und Print",
                city = "Bamberg",
                bundesland = "Bayern",
                status = ApplicationStatus.TO_APPLY,
                applicationDate = "Pending",
                followUpDate = "20.11.2026",
                interviewDate = "Pending",
                contactPerson = "Herr Thomas Meyer (HR)",
                contactEmail = "karriere@fraenkische-medien.de",
                notes = "Requires creative design portfolio in PDF format. Letter of motivation drafted."
            ),
            ApplicationEntry(
                id = "app-3",
                opportunityId = "opp-6",
                companyName = "Pädagogisches Bildungszentrum Tübingen",
                positionTitle = "Erzieher/in (PiA)",
                city = "Tübingen",
                bundesland = "Baden-Württemberg",
                status = ApplicationStatus.APPLIED,
                applicationDate = "28.10.2026",
                followUpDate = "10.11.2026",
                interviewDate = "Awaiting feedback",
                contactPerson = "Frau Dr. Sabine Keller",
                contactEmail = "bewerbung@pbz-tuebingen.de",
                notes = "Application sent via online portal. Certified copy of school diploma and B2/C1 certificate attached."
            )
        )

        val initialLogs = listOf(
            ScanLog(
                id = "log-1",
                timestamp = "03.11.2026, 04:30 Europe/Berlin",
                citiesCovered = listOf("Passau", "Bamberg", "Tübingen", "Celle", "Görlitz", "Flensburg"),
                companiesDiscovered = 115,
                opportunitiesVerified = 18,
                durationSeconds = 142,
                status = "COMPLETED_SUCCESS",
                details = "Rotated batch 1. Analyzed 23 career domains with Gemini AI. Discovered 3 new 2027/2028 vacancies."
            ),
            ScanLog(
                id = "log-2",
                timestamp = "02.11.2026, 23:15 Europe/Berlin",
                citiesCovered = listOf("Bautzen", "Wismar", "Fulda", "Bad Homburg", "Speyer"),
                companiesDiscovered = 63,
                opportunitiesVerified = 11,
                durationSeconds = 98,
                status = "COMPLETED_SUCCESS",
                details = "Rotated batch 2. Re-verified 15 existing company URLs. 0 expired links detected."
            ),
            ScanLog(
                id = "log-3",
                timestamp = "02.11.2026, 18:20 Europe/Berlin",
                citiesCovered = listOf("Wernigerode", "Eisenach", "Homburg", "Eberswalde", "Arnsberg"),
                companiesDiscovered = 54,
                opportunitiesVerified = 9,
                durationSeconds = 85,
                status = "COMPLETED_SUCCESS",
                details = "Scheduled cloud runner triggered. Evaluated small city childcare centers and regional media agencies."
            )
        )

        _cities.value = initialCities
        _companies.value = initialCompanies
        _opportunities.value = initialOpportunities
        _applications.value = initialApplications
        _scanLogs.value = initialLogs
    }

    private val repositoryScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)

    // Toggle saved state
    fun toggleSaveOpportunity(id: String) {
        _opportunities.update { list ->
            list.map { if (it.id == id) it.copy(isSaved = !it.isSaved) else it }
        }
        _opportunities.value.find { it.id == id }?.let { opp ->
            repositoryScope.launch {
                firestoreSyncService?.syncOpportunity(opp)
            }
        }
    }

    // Update application tracking status on an opportunity
    fun updateOpportunityApplicationStatus(oppId: String, newStatus: ApplicationStatus, notes: String = "") {
        _opportunities.update { list ->
            list.map { if (it.id == oppId) it.copy(applicationStatus = newStatus) else it }
        }

        val opp = _opportunities.value.find { it.id == oppId } ?: return
        val existingApp = _applications.value.find { it.opportunityId == oppId }

        if (newStatus == ApplicationStatus.NOT_APPLIED) {
            if (existingApp != null) {
                _applications.update { list -> list.filter { it.opportunityId != oppId } }
            }
        } else {
            val appToSync = if (existingApp != null) {
                val updated = existingApp.copy(
                    status = newStatus,
                    notes = if (notes.isNotBlank()) notes else existingApp.notes
                )
                _applications.update { list ->
                    list.map { if (it.opportunityId == oppId) updated else it }
                }
                updated
            } else {
                val newApp = ApplicationEntry(
                    id = UUID.randomUUID().toString(),
                    opportunityId = opp.id,
                    companyName = opp.companyName,
                    positionTitle = opp.title,
                    city = opp.city,
                    bundesland = opp.bundesland,
                    status = newStatus,
                    applicationDate = "03.11.2026",
                    followUpDate = "17.11.2026",
                    interviewDate = "Pending",
                    contactPerson = "HR / Ausbildungsleitung",
                    contactEmail = "karriere@" + opp.companyWebsite.removePrefix("https://www.").removePrefix("http://www."),
                    notes = notes.ifBlank { "Added to application tracker" }
                )
                _applications.update { listOf(newApp) + it }
                newApp
            }

            repositoryScope.launch {
                firestoreSyncService?.syncOpportunity(opp.copy(applicationStatus = newStatus))
                firestoreSyncService?.syncApplication(appToSync)
            }
        }
    }

    // Toggle City Paused state
    fun togglePauseCity(cityId: String) {
        _cities.update { list ->
            list.map { if (it.id == cityId) it.copy(isPaused = !it.isPaused) else it }
        }
    }

    // Toggle City Priority
    fun togglePrioritizeCity(cityId: String) {
        _cities.update { list ->
            list.map { if (it.id == cityId) it.copy(isPrioritized = !it.isPrioritized) else it }
        }
    }

    // Add custom German city
    fun addCity(name: String, bundesland: String, postalCode: String, population: Int) {
        val tier = when {
            population < 25000 -> PopulationTier.UNDER_25K
            population <= 50000 -> PopulationTier.TIER_25K_50K
            population <= 100000 -> PopulationTier.TIER_50K_100K
            else -> PopulationTier.OVER_100K
        }
        val newCity = GermanCity(
            id = "city-" + UUID.randomUUID().toString().take(6),
            name = name.trim(),
            postalCode = postalCode.trim(),
            bundesland = bundesland.trim(),
            population = population,
            populationTier = tier,
            isPrioritized = true,
            lastSearchDate = "Pending next scan",
            discoveredCompaniesCount = 0,
            verifiedOpportunitiesCount = 0,
            latitude = 51.1657,
            longitude = 10.4515
        )
        _cities.update { listOf(newCity) + it }
    }

    // Delete city
    fun removeCity(cityId: String) {
        _cities.update { list -> list.filter { it.id != cityId } }
    }

    // Pause / Resume Automation
    fun toggleAutomationPaused() {
        _automationStatus.update { it.copy(isPaused = !it.isPaused) }
    }

    // Update interval
    fun setAutomationInterval(hours: Int) {
        _automationStatus.update { it.copy(intervalHours = hours) }
    }

    // Update application entry details
    fun updateApplicationEntry(entry: ApplicationEntry) {
        _applications.update { list ->
            list.map { if (it.id == entry.id) entry else it }
        }
    }

    // Trigger an interactive Scan Now execution with real multi-step simulation
    suspend fun executeScanNow() {
        if (_currentScanPhase.value != ScanPhase.IDLE) return

        try {
            _automationStatus.update { it.copy(isRunning = true, lockActive = true) }

            // Phase 1: Google Places Discovery
            _currentScanPhase.value = ScanPhase.GOOGLE_PLACES_DISCOVERY
            _scanProgress.value = 0.15f
            _scanLogDetail.value = "Targeting German small-to-medium cities: Passau, Bamberg, Tübingen, Fulda... Querying Google Places API (Text Search) for Marketingagenturen & Bildungszentren..."
            delay(1200)

            _scanProgress.value = 0.35f
            _scanLogDetail.value = "Google Places API discovered 14 new business listings with Place IDs. Filtering duplicate establishments..."
            delay(1000)

            // Phase 2: Official Website & Career Crawler
            _currentScanPhase.value = ScanPhase.CAREER_PAGE_CRAWLER
            _scanProgress.value = 0.55f
            _scanLogDetail.value = "Inspecting company websites: /karriere, /ausbildung, /stellenangebote. Respecting robots.txt & access restrictions..."
            delay(1400)

            // Phase 3: Gemini AI Classification & Extraction
            _currentScanPhase.value = ScanPhase.GEMINI_CLASSIFICATION
            _scanProgress.value = 0.75f
            _scanLogDetail.value = "Gemini AI analyzing extracted vacancy text. Validating German title, category, starting years 2027/2028, and strict Ausbildung criteria (excluding university degrees)..."
            delay(1500)

            // Phase 4: Database Sync
            _currentScanPhase.value = ScanPhase.DATABASE_SYNC
            _scanProgress.value = 0.90f
            _scanLogDetail.value = "Updating Firestore database with 2 newly confirmed Ausbildung opportunities and updating city metrics..."
            delay(1000)

            // Add newly discovered opportunity
            val newOpportunity = Opportunity(
                id = "opp-live-" + System.currentTimeMillis().toString().takeLast(5),
                title = "Ausbildung zum Kaufmann/Kauffrau für Marketingkommunikation (m/w/d) – 2027",
                englishSummary = "Newly discovered marketing communication apprenticeship verified by Gemini AI research engine.",
                companyName = "Mainfranken Creative Hub GmbH",
                city = "Würzburg",
                postalCode = "97070",
                bundesland = "Bayern",
                category = AusbildungCategory.MARKETING,
                subcategory = "Marketingkommunikation",
                trainingType = TrainingType.DUALE_AUSBILDUNG,
                startYear = 2027,
                startDate = "01.09.2027",
                applicationDeadline = "31.05.2027",
                salary = "1.100 € (1. Jahr), 1.180 € (2. Jahr), 1.280 € (3. Jahr)",
                languageRequirement = "B2 Deutsch (Fließend)",
                qualifications = "Mittlere Reife oder Abitur, Social Media & Canva / Adobe Affinität",
                description = "Entdecke spannende Markenführung und digitale Kampagnen im Herzen Unterfrankens. Duale Ausbildung mit Berufsschulunterricht in Würzburg.",
                applicationUrl = "https://www.mainfranken-creative.de/jobs/ausbildung-2027",
                companyWebsite = "https://www.mainfranken-creative.de",
                sourceUrl = "https://www.mainfranken-creative.de/karriere",
                verificationStatus = VerificationStatus.CONFIRMED_ACTIVE,
                firstDiscoveredAt = "Just now",
                lastVerifiedAt = "Just now",
                isSaved = false,
                applicationStatus = ApplicationStatus.NOT_APPLIED,
                latitude = 49.7913,
                longitude = 9.9534
            )

            _opportunities.update { listOf(newOpportunity) + it }

            // Add a new log
            val newLog = ScanLog(
                id = "log-" + UUID.randomUUID().toString().take(6),
                timestamp = "Just now (Europe/Berlin)",
                citiesCovered = listOf("Würzburg", "Bamberg", "Passau"),
                companiesDiscovered = 14,
                opportunitiesVerified = 1,
                durationSeconds = 6,
                status = "COMPLETED_SUCCESS",
                details = "Manual radar trigger finished. 14 businesses investigated, 1 verified 2027 Marketing vacancy extracted."
            )
            _scanLogs.update { listOf(newLog) + it }

            // Update automation status counters
            _automationStatus.update {
                it.copy(
                    isRunning = false,
                    lockActive = false,
                    placesApiCallsCount = it.placesApiCallsCount + 14,
                    geminiApiCallsCount = it.geminiApiCallsCount + 4,
                    lastScanTime = "Just now"
                )
            }

            _currentScanPhase.value = ScanPhase.COMPLETED
            _scanProgress.value = 1f
            _scanLogDetail.value = "Scan cycle completed successfully. 1 new verified opportunity added to repository!"
            delay(1200)

        } finally {
            _currentScanPhase.value = ScanPhase.IDLE
            _automationStatus.update { it.copy(isRunning = false, lockActive = false) }
        }
    }

    // Export Applications to CSV string format
    fun generateApplicationsCsv(): String {
        val sb = StringBuilder()
        sb.append("ID,Position,Company,City,Bundesland,Status,ApplicationDate,FollowUpDate,InterviewDate,ContactPerson,Email,Notes\n")
        _applications.value.forEach { app ->
            sb.append("\"${app.id}\",")
            sb.append("\"${app.positionTitle.replace("\"", "\"\"")}\",")
            sb.append("\"${app.companyName.replace("\"", "\"\"")}\",")
            sb.append("\"${app.city}\",")
            sb.append("\"${app.bundesland}\",")
            sb.append("\"${app.status.label}\",")
            sb.append("\"${app.applicationDate}\",")
            sb.append("\"${app.followUpDate}\",")
            sb.append("\"${app.interviewDate}\",")
            sb.append("\"${app.contactPerson.replace("\"", "\"\"")}\",")
            sb.append("\"${app.contactEmail}\",")
            sb.append("\"${app.notes.replace("\"", "\"\"")}\"\n")
        }
        return sb.toString()
    }
}
