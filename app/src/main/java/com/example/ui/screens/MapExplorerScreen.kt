package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.GermanCity
import com.example.data.model.Opportunity
import com.example.ui.components.GermanMapVisualizer
import com.example.ui.components.OpportunityCard

@Composable
fun MapExplorerScreen(
    cities: List<GermanCity>,
    opportunities: List<Opportunity>,
    onSaveToggle: (String) -> Unit,
    onTrackClick: (Opportunity) -> Unit,
    onCardClick: (Opportunity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_explorer_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GermanMapVisualizer(
                cities = cities,
                opportunities = opportunities,
                onOpportunitySelected = onCardClick
            )
        }

        item {
            Text(
                text = "All Geocoded Opportunities (${opportunities.size})",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(opportunities, key = { it.id }) { opp ->
            OpportunityCard(
                opportunity = opp,
                onSaveToggle = onSaveToggle,
                onTrackClick = onTrackClick,
                onCardClick = onCardClick
            )
        }
    }
}
