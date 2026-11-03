package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.OpportunityCard
import com.example.ui.components.RadarSweepAnimation
import com.example.ui.theme.*

@Composable
fun OverviewScreen(
    opportunities: List<Opportunity>,
    companies: List<Company>,
    cities: List<GermanCity>,
    automationStatus: AutomationStatus,
    onScanNowClick: () -> Unit,
    onSaveToggle: (String) -> Unit,
    onTrackClick: (Opportunity) -> Unit,
    onCardClick: (Opportunity) -> Unit,
    onNavigateToOpportunities: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCompanies = companies.size
    val confirmedActive = opportunities.count { it.verificationStatus == VerificationStatus.CONFIRMED_ACTIVE }
    val futureIntakes = opportunities.count { it.verificationStatus == VerificationStatus.FUTURE_OPPORTUNITY }
    val marketingCount = opportunities.count { it.category == AusbildungCategory.MARKETING }
    val educationCount = opportunities.count { it.category == AusbildungCategory.EDUCATION }
    val count2027 = opportunities.count { it.startYear == 2027 }
    val count2028 = opportunities.count { it.startYear == 2028 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("overview_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Radar Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                border = BorderStroke(1.dp, RadarCyanGlow.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RadarCyan.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, RadarCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "AUTOMATED RESEARCH ASSISTANT • GERMANY",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RadarCyanGlow,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Ausbildung Radar",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Discovering genuine Marketing & Education apprenticeships across all 16 German states.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onScanNowClick,
                                modifier = Modifier.testTag("hero_scan_now_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = RadarCyanGlow),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Filled.Radar, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Now", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onNavigateToOpportunities,
                                modifier = Modifier.testTag("hero_view_all_button"),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("View All (${opportunities.size})")
                            }
                        }
                    }

                    // Mini live animated radar scanner
                    RadarSweepAnimation(
                        modifier = Modifier
                            .size(110.dp)
                            .padding(start = 12.dp),
                        isScanning = automationStatus.isRunning
                    )
                }
            }
        }

        // Phase 1 Demo Data Disclaimer banner
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Phase 1 Demonstration Mode: Data includes genuine German companies and public registry training profiles. Live automation runs on a 5-hour cloud schedule.",
                        style = MaterialTheme.typography.labelSmall.copy(lineHeight = 15.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "System Telemetry & Overview",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatMetricCard(
                        title = "Companies (Demo)",
                        value = totalCompanies.toString(),
                        subtitle = "Curated Registry Sample",
                        icon = Icons.Filled.Business,
                        accentColor = RadarCyanGlow,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Confirmed Active",
                        value = confirmedActive.toString(),
                        subtitle = "Demo: $confirmedActive | Live: 0",
                        icon = Icons.Filled.CheckCircle,
                        accentColor = StatusActiveGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatMetricCard(
                        title = "Future 2027 / 2028",
                        value = (count2027 + count2028).toString(),
                        subtitle = "2027: $count2027 | 2028: $count2028",
                        icon = Icons.Filled.CalendarToday,
                        accentColor = StatusFutureBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        title = "Cities Monitored",
                        value = cities.size.toString(),
                        subtitle = "All 16 Bundesländer",
                        icon = Icons.Filled.LocationCity,
                        accentColor = MarketingAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Category Split & Starting Year Cards
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Category Split Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Target Categories",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        CategoryProgressBar(
                            label = "Marketing & Media",
                            count = marketingCount,
                            total = opportunities.size,
                            color = MarketingAmber
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryProgressBar(
                            label = "Education & Childcare",
                            count = educationCount,
                            total = opportunities.size,
                            color = EducationEmerald
                        )
                    }
                }

                // Schedule & Next Run Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Cloud Scheduler",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MarketingAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "NOT CONFIGURED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MarketingAmber
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Schedule: 5h Cron (Pending)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Timezone: Europe/Berlin",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Recent Discovered Opportunities Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recently Verified Opportunities",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                TextButton(onClick = onNavigateToOpportunities) {
                    Text("See All")
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Recent Opportunities list (first 3)
        items(opportunities.take(3), key = { it.id }) { opp ->
            OpportunityCard(
                opportunity = opp,
                onSaveToggle = onSaveToggle,
                onTrackClick = onTrackClick,
                onCardClick = onCardClick
            )
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryProgressBar(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val pct = if (total > 0) count.toFloat() / total else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium))
            Text("$count (${(pct * 100).toInt()}%)", style = MaterialTheme.typography.labelSmall.copy(color = color))
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
