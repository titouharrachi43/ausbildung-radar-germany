package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ScanPhase
import com.example.ui.theme.RadarCyanGlow
import com.example.ui.theme.StatusActiveGreen

@Composable
fun ScanProgressDialog(
    phase: ScanPhase,
    progress: Float,
    logDetail: String,
    onDismiss: () -> Unit
) {
    if (phase == ScanPhase.IDLE) return

    Dialog(
        onDismissRequest = { /* Prevent dismiss while running */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("scan_progress_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, RadarCyanGlow.copy(alpha = 0.6f)),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with mini animated radar
                Box(contentAlignment = Alignment.Center) {
                    RadarSweepAnimation(
                        modifier = Modifier.size(90.dp),
                        isScanning = true
                    )
                }

                Text(
                    text = "Ausbildung Radar Active",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = when (phase) {
                        ScanPhase.GOOGLE_PLACES_DISCOVERY -> "Step 1/4: Discovering Businesses via Google Places API"
                        ScanPhase.CAREER_PAGE_CRAWLER -> "Step 2/4: Crawling Official Career & Job Portals"
                        ScanPhase.GEMINI_CLASSIFICATION -> "Step 3/4: Gemini AI Extraction & Verification"
                        ScanPhase.DATABASE_SYNC -> "Step 4/4: Syncing with Opportunity Database"
                        ScanPhase.COMPLETED -> "Scan Finished! Verified Opportunities Added"
                        else -> "Initializing Radar..."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                // Linear Progress Indicator
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = RadarCyanGlow,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(progress * 100).toInt()}% completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                // Log Detail Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Terminal,
                            contentDescription = null,
                            tint = RadarCyanGlow,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Text(
                            text = logDetail,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Dismiss button if completed
                AnimatedVisibility(visible = phase == ScanPhase.COMPLETED) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusActiveGreen)
                    ) {
                        Icon(Icons.Filled.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Newly Discovered Results")
                    }
                }
            }
        }
    }
}
