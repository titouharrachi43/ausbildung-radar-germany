package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GermanCity
import com.example.data.model.PopulationTier
import com.example.ui.theme.RadarCyanGlow
import com.example.ui.theme.MarketingAmber
import com.example.ui.theme.StatusActiveGreen

@Composable
fun CitiesScreen(
    cities: List<GermanCity>,
    onTogglePause: (String) -> Unit,
    onTogglePrioritize: (String) -> Unit,
    onRemoveCity: (String) -> Unit,
    onAddCity: (String, String, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTier by remember { mutableStateOf<PopulationTier?>(null) }
    var selectedBundesland by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val bundeslaender = remember(cities) {
        listOf("All States") + cities.map { it.bundesland }.distinct().sorted()
    }

    val filteredCities = remember(cities, searchQuery, selectedTier, selectedBundesland) {
        cities.filter { city ->
            val matchesQuery = searchQuery.isBlank() ||
                    city.name.contains(searchQuery, ignoreCase = true) ||
                    city.postalCode.contains(searchQuery) ||
                    city.bundesland.contains(searchQuery, ignoreCase = true)
            val matchesTier = selectedTier == null || city.populationTier == selectedTier
            val matchesState = selectedBundesland == null || selectedBundesland == "All States" || city.bundesland == selectedBundesland
            matchesQuery && matchesTier && matchesState
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cities_screen")
    ) {
        // Search & Add Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search German city, PLZ or state...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.weight(1f).testTag("cities_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(52.dp).testTag("add_city_button"),
                contentPadding = PaddingValues(horizontal = 14.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add City")
            }
        }

        // Tier Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTier == null,
                onClick = { selectedTier = null },
                label = { Text("All Populations (${cities.size})") }
            )
            PopulationTier.values().forEach { tier ->
                FilterChip(
                    selected = selectedTier == tier,
                    onClick = { selectedTier = if (selectedTier == tier) null else tier },
                    label = { Text(tier.label, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Count & Info header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredCities.size} cities in rotation • Official Pop. Registry",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Prioritizing <100k inhabitants",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = RadarCyanGlow)
            )
        }

        // City Cards List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredCities, key = { it.id }) { city ->
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("city_card_${city.name}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (city.isPaused) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (city.isPrioritized) RadarCyanGlow.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = city.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (city.isPrioritized) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = RadarCyanGlow.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "PRIORITY",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = RadarCyanGlow
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (city.isPaused) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "PAUSED",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.error
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "PLZ ${city.postalCode} • ${city.bundesland} • Inhabitants: ${String.format("%,d", city.population)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { onTogglePrioritize(city.id) }) {
                                    Icon(
                                        imageVector = if (city.isPrioritized) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "Toggle priority",
                                        tint = if (city.isPrioritized) MarketingAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onTogglePause(city.id) }) {
                                    Icon(
                                        imageVector = if (city.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                        contentDescription = "Toggle pause",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Discovered: ${city.discoveredCompaniesCount} companies",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Verified: ${city.verifiedOpportunitiesCount} vacancies",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = StatusActiveGreen
                            )
                            Text(
                                text = "Last Scan: ${city.lastSearchDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Add City Dialog
    if (showAddDialog) {
        var cityName by remember { mutableStateOf("") }
        var postalCode by remember { mutableStateOf("") }
        var bundesland by remember { mutableStateOf("Bayern") }
        var populationStr by remember { mutableStateOf("45000") }

        val allStates = listOf(
            "Baden-Württemberg", "Bayern", "Berlin", "Brandenburg", "Bremen",
            "Hamburg", "Hessen", "Mecklenburg-Vorpommern", "Niedersachsen",
            "Nordrhein-Westfalen", "Rheinland-Pfalz", "Saarland", "Sachsen",
            "Sachsen-Anhalt", "Schleswig-Holstein", "Thüringen"
        )
        var stateExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add German City to Radar") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = cityName,
                        onValueChange = { cityName = it },
                        label = { Text("City Name (e.g. Coburg, Passau)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("Postal Code / PLZ (e.g. 96450)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { stateExpanded = true },
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Bundesland: $bundesland")
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = stateExpanded,
                            onDismissRequest = { stateExpanded = false }
                        ) {
                            allStates.forEach { state ->
                                DropdownMenuItem(
                                    text = { Text(state) },
                                    onClick = {
                                        bundesland = state
                                        stateExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = populationStr,
                        onValueChange = { populationStr = it },
                        label = { Text("Population (Inhabitants)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pop = populationStr.toIntOrNull() ?: 25000
                        if (cityName.isNotBlank()) {
                            onAddCity(cityName, bundesland, postalCode, pop)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add to Database")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
