package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AusbildungCategory
import com.example.data.model.GermanCity
import com.example.data.model.Opportunity
import com.example.ui.theme.*
import kotlin.math.abs

@Composable
fun GermanMapVisualizer(
    cities: List<GermanCity>,
    opportunities: List<Opportunity>,
    onOpportunitySelected: (Opportunity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<AusbildungCategory?>(null) }
    var selectedCity by remember { mutableStateOf<GermanCity?>(null) }
    var hoveredOpportunity by remember { mutableStateOf<Opportunity?>(null) }

    val filteredOpportunities = remember(opportunities, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) opportunities
        else opportunities.filter { it.category == selectedCategoryFilter }
    }

    // Germany bounding box:
    // Latitude: min ~47.3 (South, Passau/Bavaria), max ~55.0 (North, Flensburg)
    // Longitude: min ~6.0 (West), max ~15.1 (East, Görlitz)
    val minLat = 47.2
    val maxLat = 55.2
    val minLon = 5.8
    val maxLon = 15.3

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("german_map_explorer"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Filter Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Germany Map Explorer",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All (${opportunities.size})", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == AusbildungCategory.MARKETING,
                        onClick = { selectedCategoryFilter = AusbildungCategory.MARKETING },
                        label = { Text("Marketing", style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MarketingAmber.copy(alpha = 0.2f),
                            selectedLabelColor = MarketingAmber
                        )
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == AusbildungCategory.EDUCATION,
                        onClick = { selectedCategoryFilter = AusbildungCategory.EDUCATION },
                        label = { Text("Education", style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EducationEmerald.copy(alpha = 0.2f),
                            selectedLabelColor = EducationEmerald
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Map Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(filteredOpportunities, cities) {
                            detectTapGestures { tapOffset ->
                                val w = size.width
                                val h = size.height

                                // Find closest city or opportunity within 30dp radius
                                var closestOpp: Opportunity? = null
                                var minOppDist = 45f

                                filteredOpportunities.forEach { opp ->
                                    val nx = ((opp.longitude - minLon) / (maxLon - minLon)).toFloat()
                                    val ny = (1f - ((opp.latitude - minLat) / (maxLat - minLat))).toFloat()
                                    val px = nx * w
                                    val py = ny * h

                                    val dist = kotlin.math.sqrt(
                                        (tapOffset.x - px) * (tapOffset.x - px) +
                                                (tapOffset.y - py) * (tapOffset.y - py)
                                    )
                                    if (dist < minOppDist) {
                                        minOppDist = dist
                                        closestOpp = opp
                                    }
                                }

                                if (closestOpp != null) {
                                    hoveredOpportunity = closestOpp
                                    val matchCity = cities.find { it.name.equals(closestOpp?.city, ignoreCase = true) }
                                    selectedCity = matchCity
                                } else {
                                    // Check if tapped a city
                                    var closestCity: GermanCity? = null
                                    var minCityDist = 40f
                                    cities.forEach { c ->
                                        val nx = ((c.longitude - minLon) / (maxLon - minLon)).toFloat()
                                        val ny = (1f - ((c.latitude - minLat) / (maxLat - minLat))).toFloat()
                                        val px = nx * w
                                        val py = ny * h
                                        val dist = kotlin.math.sqrt(
                                            (tapOffset.x - px) * (tapOffset.x - px) +
                                                    (tapOffset.y - py) * (tapOffset.y - py)
                                        )
                                        if (dist < minCityDist) {
                                            minCityDist = dist
                                            closestCity = c
                                        }
                                    }
                                    selectedCity = closestCity
                                    hoveredOpportunity = filteredOpportunities.find { it.city == closestCity?.name }
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // Draw stylized Germany regional boundary lines
                    val boundaryColor = RadarDarkBorder.copy(alpha = 0.45f)

                    // Draw subtle grid
                    for (x in 1..4) {
                        drawLine(
                            color = boundaryColor.copy(alpha = 0.15f),
                            start = Offset(w * (x / 5f), 0f),
                            end = Offset(w * (x / 5f), h),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 1..4) {
                        drawLine(
                            color = boundaryColor.copy(alpha = 0.15f),
                            start = Offset(0f, h * (y / 5f)),
                            end = Offset(w, h * (y / 5f)),
                            strokeWidth = 1f
                        )
                    }

                    // Stylized boundary polygon of Germany
                    val polyPoints = listOf(
                        Offset(0.40f * w, 0.05f * h), // Flensburg / Baltic coast
                        Offset(0.65f * w, 0.12f * h), // Rügen / MV
                        Offset(0.85f * w, 0.28f * h), // Frankfurt (Oder)
                        Offset(0.96f * w, 0.52f * h), // Görlitz / Zittau
                        Offset(0.80f * w, 0.90f * h), // Passau / Berchtesgaden
                        Offset(0.50f * w, 0.98f * h), // Allgäu / Bodensee
                        Offset(0.24f * w, 0.88f * h), // Freiburg / Black Forest
                        Offset(0.12f * w, 0.65f * h), // Saarland / Trier
                        Offset(0.08f * w, 0.42f * h), // Aachen / NRW
                        Offset(0.20f * w, 0.16f * h), // East Frisia / Bremen
                        Offset(0.40f * w, 0.05f * h)  // Close loop
                    )

                    val mapPath = Path().apply {
                        moveTo(polyPoints[0].x, polyPoints[0].y)
                        for (i in 1 until polyPoints.size) {
                            lineTo(polyPoints[i].x, polyPoints[i].y)
                        }
                    }

                    drawPath(
                        path = mapPath,
                        color = RadarCyan.copy(alpha = 0.05f)
                    )
                    drawPath(
                        path = mapPath,
                        color = RadarCyanGlow.copy(alpha = 0.35f),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw all cities as radar base nodes
                    cities.forEach { city ->
                        val nx = ((city.longitude - minLon) / (maxLon - minLon)).toFloat()
                        val ny = (1f - ((city.latitude - minLat) / (maxLat - minLat))).toFloat()
                        val px = nx * w
                        val py = ny * h

                        val isSel = selectedCity?.name == city.name
                        drawCircle(
                            color = if (isSel) RadarCyanGlow else Color.Gray.copy(alpha = 0.4f),
                            radius = if (isSel) 7.dp.toPx() else 3.5.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }

                    // Draw discovered Ausbildung opportunity pins
                    filteredOpportunities.forEach { opp ->
                        val nx = ((opp.longitude - minLon) / (maxLon - minLon)).toFloat()
                        val ny = (1f - ((opp.latitude - minLat) / (maxLat - minLat))).toFloat()
                        val px = nx * w
                        val py = ny * h

                        val isHovered = hoveredOpportunity?.id == opp.id
                        val pinColor = if (opp.category == AusbildungCategory.MARKETING) MarketingAmber else EducationEmerald

                        // Glowing outer ring for opportunity pins
                        drawCircle(
                            color = pinColor.copy(alpha = if (isHovered) 0.5f else 0.25f),
                            radius = if (isHovered) 12.dp.toPx() else 8.dp.toPx(),
                            center = Offset(px, py)
                        )
                        // Core dot
                        drawCircle(
                            color = pinColor,
                            radius = if (isHovered) 6.dp.toPx() else 4.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }
                }

                // Legend in bottom-left
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(MarketingAmber, CircleShape))
                            Text("Marketing", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(EducationEmerald, CircleShape))
                            Text("Education", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive info inspector card
            val activeOpp = hoveredOpportunity ?: filteredOpportunities.firstOrNull()
            if (activeOpp != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "${activeOpp.city}, ${activeOpp.bundesland}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "• Start ${activeOpp.startYear}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = activeOpp.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Text(
                                text = "${activeOpp.companyName} | ${activeOpp.salary}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = { onOpportunitySelected(activeOpp) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Inspect", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
