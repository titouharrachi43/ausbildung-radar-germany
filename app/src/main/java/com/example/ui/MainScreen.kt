package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ScanPhase
import com.example.ui.components.OpportunityDetailModal
import com.example.ui.components.ScanProgressDialog
import com.example.ui.screens.*
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.RadarCyanGlow
import com.example.ui.theme.StatusActiveGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val opportunities by viewModel.opportunities.collectAsStateWithLifecycle()
    val companies by viewModel.companies.collectAsStateWithLifecycle()
    val cities by viewModel.cities.collectAsStateWithLifecycle()
    val applications by viewModel.applications.collectAsStateWithLifecycle()
    val scanLogs by viewModel.scanLogs.collectAsStateWithLifecycle()
    val automationStatus by viewModel.automationStatus.collectAsStateWithLifecycle()
    val scanPhase by viewModel.currentScanPhase.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val scanLogDetail by viewModel.scanLogDetail.collectAsStateWithLifecycle()
    val inspectedOpp by viewModel.inspectedOpportunity.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle system back navigation to return to Overview screen
    BackHandler(enabled = currentTab != NavigationTab.OVERVIEW || inspectedOpp != null) {
        if (inspectedOpp != null) {
            viewModel.closeOpportunityDetail()
        } else {
            viewModel.selectTab(NavigationTab.OVERVIEW)
        }
    }

    // Modal Navigation Drawer for complete 10-section access
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.widthIn(max = 310.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = RadarCyan.copy(alpha = 0.2f),
                            border = BorderStroke(1.5.dp, RadarCyanGlow),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Radar,
                                    contentDescription = null,
                                    tint = RadarCyanGlow,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Ausbildung Radar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "Germany • 2027/2028",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "RESEARCH ENGINE NAVIGATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        val badgeCount = when (tab) {
                            NavigationTab.OPPORTUNITIES -> opportunities.size
                            NavigationTab.SAVED_JOBS -> opportunities.count { it.isSaved }
                            NavigationTab.APPLICATIONS -> applications.size
                            NavigationTab.CITIES -> cities.size
                            NavigationTab.COMPANIES -> companies.size
                            else -> null
                        }

                        NavigationDrawerItem(
                            label = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                    if (badgeCount != null && badgeCount > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = badgeCount.toString(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = getTabIcon(tab),
                                    contentDescription = tab.label,
                                    tint = if (isSelected) RadarCyanGlow else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                viewModel.selectTab(tab)
                                scope.launch { drawerState.close() }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .testTag("drawer_item_${tab.name.lowercase()}")
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Administrator Google account footer
                    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                    val isSigningIn by viewModel.isSigningIn.collectAsStateWithLifecycle()
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val activity = context as? android.app.Activity

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (currentUser != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.AccountCircle,
                                        contentDescription = null,
                                        tint = StatusActiveGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = currentUser?.displayName ?: "Firebase Administrator",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = currentUser?.email ?: "",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = StatusActiveGreen.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "FIRESTORE SYNCED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusActiveGreen
                                            ),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }

                                    TextButton(
                                        onClick = { viewModel.signOut() },
                                        contentPadding = PaddingValues(horizontal = 6.dp)
                                    ) {
                                        Text("Sign Out", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text("Firebase Authentication", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                    Text(
                                        text = "Sign in with Google to sync discovered opportunities to Firestore.",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(
                                        onClick = {
                                            if (activity != null) {
                                                viewModel.signInWithGoogle(activity)
                                            }
                                        },
                                        enabled = !isSigningIn,
                                        modifier = Modifier.fillMaxWidth().height(38.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        if (isSigningIn) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        } else {
                                            Icon(Icons.Filled.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Sign in with Google", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Ausbildung Radar",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RadarCyan.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, RadarCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "DE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = RadarCyanGlow
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open navigation menu")
                        }
                    },
                    actions = {
                        // Quick manual scan button in top bar
                        IconButton(
                            onClick = { viewModel.triggerScanNow() },
                            enabled = !automationStatus.isRunning,
                            modifier = Modifier.testTag("top_bar_scan_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Radar,
                                contentDescription = "Scan German cities now",
                                tint = if (automationStatus.isRunning) Color.Gray else RadarCyanGlow
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                // Secondary horizontal quick navigation bar for the 5 most common tabs + More button
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    val primaryTabs = listOf(
                        NavigationTab.OVERVIEW,
                        NavigationTab.OPPORTUNITIES,
                        NavigationTab.MAP_EXPLORER,
                        NavigationTab.APPLICATIONS,
                        NavigationTab.AUTOMATION
                    )

                    primaryTabs.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = getTabIcon(tab),
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                        )
                    }

                    // More button opens full drawer
                    NavigationBarItem(
                        selected = currentTab !in primaryTabs,
                        onClick = { scope.launch { drawerState.open() } },
                        icon = {
                            Icon(Icons.Filled.MoreHoriz, contentDescription = "More sections")
                        },
                        label = { Text("More", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("nav_item_more")
                    )
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = modifier
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Crossfade(targetState = currentTab, label = "tabTransition") { tab ->
                    when (tab) {
                        NavigationTab.OVERVIEW -> {
                            OverviewScreen(
                                opportunities = opportunities,
                                companies = companies,
                                cities = cities,
                                automationStatus = automationStatus,
                                onScanNowClick = { viewModel.triggerScanNow() },
                                onSaveToggle = { viewModel.toggleSaveOpportunity(it) },
                                onTrackClick = { viewModel.inspectOpportunity(it) },
                                onCardClick = { viewModel.inspectOpportunity(it) },
                                onNavigateToOpportunities = { viewModel.selectTab(NavigationTab.OPPORTUNITIES) }
                            )
                        }
                        NavigationTab.OPPORTUNITIES -> {
                            OpportunitiesScreen(
                                opportunities = opportunities,
                                onSaveToggle = { viewModel.toggleSaveOpportunity(it) },
                                onTrackClick = { viewModel.inspectOpportunity(it) },
                                onCardClick = { viewModel.inspectOpportunity(it) }
                            )
                        }
                        NavigationTab.COMPANIES -> {
                            CompaniesScreen(companies = companies)
                        }
                        NavigationTab.CITIES -> {
                            CitiesScreen(
                                cities = cities,
                                onTogglePause = { viewModel.togglePauseCity(it) },
                                onTogglePrioritize = { viewModel.togglePrioritizeCity(it) },
                                onRemoveCity = { viewModel.removeCity(it) },
                                onAddCity = { name, state, plz, pop -> viewModel.addCity(name, state, plz, pop) }
                            )
                        }
                        NavigationTab.MAP_EXPLORER -> {
                            MapExplorerScreen(
                                cities = cities,
                                opportunities = opportunities,
                                onSaveToggle = { viewModel.toggleSaveOpportunity(it) },
                                onTrackClick = { viewModel.inspectOpportunity(it) },
                                onCardClick = { viewModel.inspectOpportunity(it) }
                            )
                        }
                        NavigationTab.SAVED_JOBS -> {
                            SavedJobsScreen(
                                savedOpportunities = opportunities.filter { it.isSaved },
                                onSaveToggle = { viewModel.toggleSaveOpportunity(it) },
                                onTrackClick = { viewModel.inspectOpportunity(it) },
                                onCardClick = { viewModel.inspectOpportunity(it) },
                                onExploreClick = { viewModel.selectTab(NavigationTab.OPPORTUNITIES) }
                            )
                        }
                        NavigationTab.APPLICATIONS -> {
                            ApplicationsScreen(
                                applications = applications,
                                onUpdateEntry = { viewModel.updateApplicationEntry(it) },
                                onExportCsv = { viewModel.exportApplicationsCsv() }
                            )
                        }
                        NavigationTab.SEARCH_HISTORY -> {
                            SearchHistoryScreen(scanLogs = scanLogs)
                        }
                        NavigationTab.AUTOMATION -> {
                            AutomationScreen(
                                automationStatus = automationStatus,
                                onTogglePause = { viewModel.toggleAutomationPaused() },
                                onSetInterval = { viewModel.setAutomationInterval(it) },
                                onScanNowClick = { viewModel.triggerScanNow() }
                            )
                        }
                        NavigationTab.SETTINGS -> {
                            SettingsScreen()
                        }
                    }
                }

                // Interactive Scan Progress Dialog
                ScanProgressDialog(
                    phase = scanPhase,
                    progress = scanProgress,
                    logDetail = scanLogDetail,
                    onDismiss = { viewModel.selectTab(NavigationTab.OPPORTUNITIES) }
                )

                // Detailed Opportunity Modal
                if (inspectedOpp != null) {
                    OpportunityDetailModal(
                        opportunity = inspectedOpp!!,
                        onDismiss = { viewModel.closeOpportunityDetail() },
                        onStatusChange = { status, notes ->
                            viewModel.updateOpportunityApplicationStatus(inspectedOpp!!.id, status, notes)
                        },
                        onSaveToggle = { viewModel.toggleSaveOpportunity(it) }
                    )
                }
            }
        }
    }
}

private fun getTabIcon(tab: NavigationTab): ImageVector {
    return when (tab) {
        NavigationTab.OVERVIEW -> Icons.Filled.Dashboard
        NavigationTab.OPPORTUNITIES -> Icons.Filled.Work
        NavigationTab.COMPANIES -> Icons.Filled.Business
        NavigationTab.CITIES -> Icons.Filled.LocationCity
        NavigationTab.MAP_EXPLORER -> Icons.Filled.Map
        NavigationTab.SAVED_JOBS -> Icons.Filled.Bookmark
        NavigationTab.APPLICATIONS -> Icons.Filled.Assignment
        NavigationTab.SEARCH_HISTORY -> Icons.Filled.History
        NavigationTab.AUTOMATION -> Icons.Filled.Radar
        NavigationTab.SETTINGS -> Icons.Filled.Settings
    }
}
