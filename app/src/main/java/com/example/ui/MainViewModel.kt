package com.example.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.AuthManager
import com.example.data.firebase.FirestoreSyncService
import com.example.data.model.*
import com.example.data.repository.AusbildungRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class NavigationTab(val label: String, val iconName: String) {
    OVERVIEW("Overview", "Dashboard"),
    OPPORTUNITIES("Opportunities", "Search"),
    COMPANIES("Companies", "Business"),
    CITIES("Cities", "LocationCity"),
    MAP_EXPLORER("Map Explorer", "Map"),
    SAVED_JOBS("Saved Jobs", "Bookmark"),
    APPLICATIONS("Applications", "Assignment"),
    SEARCH_HISTORY("Search History", "History"),
    AUTOMATION("Automation", "Radar"),
    SETTINGS("Settings", "Settings")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val authManager = AuthManager(application.applicationContext)
    private val firestoreSyncService = FirestoreSyncService(application.applicationContext)
    private val repository = AusbildungRepository(firestoreSyncService)

    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser
    val isSigningIn: StateFlow<Boolean> = authManager.isSigningIn
    val authErrorMessage: StateFlow<String?> = authManager.errorMessage

    val opportunities = repository.opportunities
    val companies = repository.companies
    val cities = repository.cities
    val applications = repository.applications
    val scanLogs = repository.scanLogs
    val automationStatus = repository.automationStatus
    val currentScanPhase = repository.currentScanPhase
    val scanProgress = repository.scanProgress
    val scanLogDetail = repository.scanLogDetail

    private val _currentTab = MutableStateFlow(NavigationTab.OVERVIEW)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _inspectedOpportunity = MutableStateFlow<Opportunity?>(null)
    val inspectedOpportunity: StateFlow<Opportunity?> = _inspectedOpportunity.asStateFlow()

    init {
        authManager.attemptAutoSignIn(viewModelScope)
    }

    fun signInWithGoogle(activity: Activity) {
        authManager.signInWithGoogle(activity, viewModelScope)
    }

    fun signOut() {
        authManager.signOut(viewModelScope)
    }

    fun clearAuthError() {
        authManager.clearError()
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun inspectOpportunity(opp: Opportunity) {
        _inspectedOpportunity.value = opp
    }

    fun closeOpportunityDetail() {
        _inspectedOpportunity.value = null
    }

    fun toggleSaveOpportunity(id: String) {
        repository.toggleSaveOpportunity(id)
        val current = _inspectedOpportunity.value
        if (current?.id == id) {
            _inspectedOpportunity.value = current.copy(isSaved = !current.isSaved)
        }
    }

    fun updateOpportunityApplicationStatus(oppId: String, status: ApplicationStatus, notes: String = "") {
        repository.updateOpportunityApplicationStatus(oppId, status, notes)
        val current = _inspectedOpportunity.value
        if (current?.id == oppId) {
            _inspectedOpportunity.value = current.copy(applicationStatus = status)
        }
    }

    fun triggerScanNow() {
        viewModelScope.launch {
            repository.executeScanNow()
        }
    }

    fun togglePauseCity(cityId: String) {
        repository.togglePauseCity(cityId)
    }

    fun togglePrioritizeCity(cityId: String) {
        repository.togglePrioritizeCity(cityId)
    }

    fun addCity(name: String, bundesland: String, postalCode: String, population: Int) {
        repository.addCity(name, bundesland, postalCode, population)
    }

    fun removeCity(cityId: String) {
        repository.removeCity(cityId)
    }

    fun toggleAutomationPaused() {
        repository.toggleAutomationPaused()
    }

    fun setAutomationInterval(hours: Int) {
        repository.setAutomationInterval(hours)
    }

    fun updateApplicationEntry(entry: ApplicationEntry) {
        repository.updateApplicationEntry(entry)
    }

    fun exportApplicationsCsv(): String {
        return repository.generateApplicationsCsv()
    }
}
