package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationEntry
import com.example.data.model.ApplicationStatus
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusFutureBlue
import com.example.ui.theme.MarketingAmber

@Composable
fun ApplicationsScreen(
    applications: List<ApplicationEntry>,
    onUpdateEntry: (ApplicationEntry) -> Unit,
    onExportCsv: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabStatus by remember { mutableStateOf<ApplicationStatus?>(null) }
    var editingEntry by remember { mutableStateOf<ApplicationEntry?>(null) }
    var showCsvDialog by remember { mutableStateOf(false) }
    var csvContent by remember { mutableStateOf("") }

    val filteredApps = remember(applications, selectedTabStatus) {
        if (selectedTabStatus == null) applications
        else applications.filter { it.status == selectedTabStatus }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("applications_screen")
    ) {
        // Header with CSV Export action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Application Pipeline",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${applications.size} active tracks across German companies",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    csvContent = onExportCsv()
                    showCsvDialog = true
                },
                modifier = Modifier.testTag("export_csv_button"),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export CSV", style = MaterialTheme.typography.labelMedium)
            }
        }

        // Status filter tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTabStatus == null,
                onClick = { selectedTabStatus = null },
                label = { Text("All (${applications.size})") }
            )
            ApplicationStatus.values().filter { it != ApplicationStatus.NOT_APPLIED }.forEach { status ->
                val count = applications.count { it.status == status }
                FilterChip(
                    selected = selectedTabStatus == status,
                    onClick = {
                        selectedTabStatus = if (selectedTabStatus == status) null else status
                    },
                    label = { Text("${status.label} ($count)", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Applications list
        if (filteredApps.isEmpty()) {
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
                        imageVector = Icons.Filled.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        text = "No applications in this category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Mark opportunities as 'Interested' or 'To Apply' to track them here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredApps, key = { it.id }) { app ->
                    val statusColor = when (app.status) {
                        ApplicationStatus.ACCEPTED -> StatusActiveGreen
                        ApplicationStatus.INTERVIEW -> MarketingAmber
                        ApplicationStatus.APPLIED -> StatusFutureBlue
                        ApplicationStatus.REJECTED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("app_entry_${app.id}"),
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
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = statusColor.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = app.status.label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = statusColor),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { editingEntry = app },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit Application Details", modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = app.positionTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${app.companyName} • ${app.city}, ${app.bundesland}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Applied: ${app.applicationDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Follow-up: ${app.followUpDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Interview: ${app.interviewDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MarketingAmber
                                )
                            }

                            if (app.contactPerson.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Contact: ${app.contactPerson} (${app.contactEmail})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (app.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Notes: ${app.notes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Application Dialog
    if (editingEntry != null) {
        val entry = editingEntry!!
        var curStatus by remember { mutableStateOf(entry.status) }
        var contactPerson by remember { mutableStateOf(entry.contactPerson) }
        var contactEmail by remember { mutableStateOf(entry.contactEmail) }
        var followUpDate by remember { mutableStateOf(entry.followUpDate) }
        var interviewDate by remember { mutableStateOf(entry.interviewDate) }
        var notes by remember { mutableStateOf(entry.notes) }
        var statusDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { editingEntry = null },
            title = { Text("Update Application Track") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${entry.positionTitle} at ${entry.companyName}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { statusDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Status: ${curStatus.label}")
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = statusDropdownExpanded,
                            onDismissRequest = { statusDropdownExpanded = false }
                        ) {
                            ApplicationStatus.values().filter { it != ApplicationStatus.NOT_APPLIED }.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.label) },
                                    onClick = {
                                        curStatus = st
                                        statusDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = contactPerson,
                        onValueChange = { contactPerson = it },
                        label = { Text("Contact Person") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = { contactEmail = it },
                        label = { Text("Contact Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = followUpDate,
                            onValueChange = { followUpDate = it },
                            label = { Text("Follow-up Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = interviewDate,
                            onValueChange = { interviewDate = it },
                            label = { Text("Interview Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Personal Notes") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = entry.copy(
                            status = curStatus,
                            contactPerson = contactPerson,
                            contactEmail = contactEmail,
                            followUpDate = followUpDate,
                            interviewDate = interviewDate,
                            notes = notes
                        )
                        onUpdateEntry(updated)
                        editingEntry = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingEntry = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // CSV Export Dialog
    if (showCsvDialog) {
        AlertDialog(
            onDismissRequest = { showCsvDialog = false },
            title = { Text("Export Applications to CSV") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Comma-Separated Values (CSV) formatted for Google Sheets or Microsoft Excel:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)
                    ) {
                        Text(
                            text = csvContent,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Ausbildung Applications CSV", csvContent)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "CSV copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showCsvDialog = false
                    }
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCsvDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
