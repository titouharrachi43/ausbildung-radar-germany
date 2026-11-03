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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.OpportunityCard
import com.example.ui.theme.*

enum class OpportunitySort(val label: String) {
    RECENTLY_DISCOVERED("Recently Discovered"),
    START_YEAR("Starting Year (2027-2028)"),
    EARLIEST_DEADLINE("Earliest Deadline"),
    CITY_NAME("City (A-Z)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpportunitiesScreen(
    opportunities: List<Opportunity>,
    onSaveToggle: (String) -> Unit,
    onTrackClick: (Opportunity) -> Unit,
    onCardClick: (Opportunity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<AusbildungCategory?>(null) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var selectedTrainingType by remember { mutableStateOf<TrainingType?>(null) }
    var selectedBundesland by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<VerificationStatus?>(null) }
    var savedOnly by remember { mutableStateOf(false) }
    var currentSort by remember { mutableStateOf(OpportunitySort.RECENTLY_DISCOVERED) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val bundeslaender = remember(opportunities) {
        listOf("All States") + opportunities.map { it.bundesland }.distinct().sorted()
    }

    val filteredList = remember(
        opportunities,
        searchQuery,
        selectedCategory,
        selectedYear,
        selectedTrainingType,
        selectedBundesland,
        selectedStatus,
        savedOnly,
        currentSort
    ) {
        opportunities.filter { opp ->
            val matchesQuery = searchQuery.isBlank() ||
                    opp.title.contains(searchQuery, ignoreCase = true) ||
                    opp.companyName.contains(searchQuery, ignoreCase = true) ||
                    opp.city.contains(searchQuery, ignoreCase = true) ||
                    opp.englishSummary.contains(searchQuery, ignoreCase = true) ||
                    opp.subcategory.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == null || opp.category == selectedCategory
            val matchesYear = selectedYear == null || opp.startYear == selectedYear
            val matchesType = selectedTrainingType == null || opp.trainingType == selectedTrainingType
            val matchesState = selectedBundesland == null || selectedBundesland == "All States" || opp.bundesland == selectedBundesland
            val matchesStatus = selectedStatus == null || opp.verificationStatus == selectedStatus
            val matchesSaved = !savedOnly || opp.isSaved

            matchesQuery && matchesCategory && matchesYear && matchesType && matchesState && matchesStatus && matchesSaved
        }.let { list ->
            when (currentSort) {
                OpportunitySort.RECENTLY_DISCOVERED -> list // preserve chronological feed
                OpportunitySort.START_YEAR -> list.sortedBy { it.startYear }
                OpportunitySort.EARLIEST_DEADLINE -> list.sortedBy { it.applicationDeadline }
                OpportunitySort.CITY_NAME -> list.sortedBy { it.city }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("opportunities_screen")
    ) {
        // Search bar & Filter Trigger
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search German title, company, city, or specialty...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_text_field"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category: All
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All Categories") }
                )

                // Category: Marketing
                FilterChip(
                    selected = selectedCategory == AusbildungCategory.MARKETING,
                    onClick = {
                        selectedCategory = if (selectedCategory == AusbildungCategory.MARKETING) null else AusbildungCategory.MARKETING
                    },
                    label = { Text("Marketing") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MarketingAmber.copy(alpha = 0.2f),
                        selectedLabelColor = MarketingAmber
                    )
                )

                // Category: Education
                FilterChip(
                    selected = selectedCategory == AusbildungCategory.EDUCATION,
                    onClick = {
                        selectedCategory = if (selectedCategory == AusbildungCategory.EDUCATION) null else AusbildungCategory.EDUCATION
                    },
                    label = { Text("Education") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EducationEmerald.copy(alpha = 0.2f),
                        selectedLabelColor = EducationEmerald
                    )
                )

                // Year 2027
                FilterChip(
                    selected = selectedYear == 2027,
                    onClick = { selectedYear = if (selectedYear == 2027) null else 2027 },
                    label = { Text("Start 2027") }
                )

                // Year 2028
                FilterChip(
                    selected = selectedYear == 2028,
                    onClick = { selectedYear = if (selectedYear == 2028) null else 2028 },
                    label = { Text("Start 2028") }
                )

                // Bookmarked
                FilterChip(
                    selected = savedOnly,
                    onClick = { savedOnly = !savedOnly },
                    label = { Text("Saved Only") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (savedOnly) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }

        // Subheader: Result Count & Sort Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredList.size} genuine opportunities found",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box {
                TextButton(
                    onClick = { showSortMenu = true },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Filled.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentSort.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    OpportunitySort.values().forEach { sortOption ->
                        DropdownMenuItem(
                            text = { Text(sortOption.label) },
                            onClick = {
                                currentSort = sortOption
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // List or Empty State
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = "No opportunities match your filters",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Try clearing search keywords or switching category filters.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedCategory = null
                            selectedYear = null
                            selectedTrainingType = null
                            selectedBundesland = null
                            selectedStatus = null
                            savedOnly = false
                        }
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredList, key = { it.id }) { opp ->
                    OpportunityCard(
                        opportunity = opp,
                        onSaveToggle = onSaveToggle,
                        onTrackClick = onTrackClick,
                        onCardClick = onCardClick
                    )
                }
            }
        }
    }
}
