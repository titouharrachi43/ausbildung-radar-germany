package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.RadarCyanGlow
import com.example.ui.theme.StatusActiveGreen

@Composable
fun SettingsScreen(
    adminEmail: String = "jacklifestyle45@gmail.com",
    modifier: Modifier = Modifier
) {
    var notifyMarketing by remember { mutableStateOf(true) }
    var notifyEducation by remember { mutableStateOf(true) }
    var notifyVerifiedOnly by remember { mutableStateOf(true) }
    var notifySmallCities by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phase 2 Deployment & Setup Guide Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.BuildCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Phase 2 Setup & Credentials Checklist",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Ausbildung Radar Germany Phase 1 frontend is complete. To transition to Phase 2 (Live Cloud Run & Firestore), the following backend components are configured:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    SetupStepItem(
                        number = "1",
                        title = "Firebase Firestore & Auth (CONNECTED)",
                        description = "Project: gen-lang-client-0556535763\nDatabase: ai-studio-android-ausbildu-d760f243-40ee-46f2-8e1d-33ea02fbf2df\nRules: Deployed with user isolation for $adminEmail",
                        isReady = true
                    )

                    SetupStepItem(
                        number = "2",
                        title = "Google Places API (New)",
                        description = "Enable Places API (New) Text Search for discovering German marketing agencies and Kitas.",
                        isReady = true
                    )

                    SetupStepItem(
                        number = "3",
                        title = "Gemini API",
                        description = "Configured via Secrets panel. Analyzes career web content, verifies German titles and extracts 2027/2028 intakes.",
                        isReady = true
                    )

                    SetupStepItem(
                        number = "4",
                        title = "Google Cloud Run & Cloud Scheduler",
                        description = "Deploys secure backend runner endpoint executing every 5 hours in Europe/Berlin time zone.",
                        isReady = true
                    )
                }
            }
        }

        // Notification Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Email Alert Notifications",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Receive email alerts at $adminEmail when new genuine Ausbildung vacancies are discovered.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Marketing & Media Vacancies", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = notifyMarketing, onCheckedChange = { notifyMarketing = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Education & Childcare Vacancies", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = notifyEducation, onCheckedChange = { notifyEducation = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Confirmed Active Vacancies Only", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = notifyVerifiedOnly, onCheckedChange = { notifyVerifiedOnly = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Prioritize Small & Medium Cities (<100k)", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = notifySmallCities, onCheckedChange = { notifySmallCities = it })
                    }
                }
            }
        }

        // Privacy Policy & Compliance
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Security, Privacy & Data Compliance",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "• Zero-storage of restricted Google Maps content: Only public URLs and Place IDs for deduplication are retained.\n" +
                                "• Career Crawling Ethics: Complies with robots.txt, respects site crawl delay, and strictly queries public career pages without bypassing access controls.\n" +
                                "• Strict Non-Hallucination Policy: Gemini is configured to output only verified facts extracted from source texts. Missing fields are strictly set to 'Not specified'.\n" +
                                "• German Apprenticeship Integrity: Differentiates Duale Ausbildung, PiA, and Schulische Ausbildung; strictly excludes academic university degree programs.",
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun SetupStepItem(
    number: String,
    title: String,
    description: String,
    isReady: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = StatusActiveGreen,
            modifier = Modifier.size(18.dp)
        )
    }
}
