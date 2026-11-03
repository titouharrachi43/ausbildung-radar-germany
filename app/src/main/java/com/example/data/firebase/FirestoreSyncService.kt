package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreSyncService(private val context: Context) {

    private val db: FirebaseFirestore by lazy {
        val databaseId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(databaseId)
    }

    private fun currentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    suspend fun syncOpportunity(opportunity: Opportunity) {
        val uid = currentUserId() ?: return
        try {
            val docRef = db.collection("users")
                .document(uid)
                .collection("opportunities")
                .document(opportunity.id)

            val data = mapOf(
                "title" to opportunity.title,
                "englishSummary" to opportunity.englishSummary,
                "companyName" to opportunity.companyName,
                "city" to opportunity.city,
                "postalCode" to opportunity.postalCode,
                "bundesland" to opportunity.bundesland,
                "category" to opportunity.category.name,
                "subcategory" to opportunity.subcategory,
                "trainingType" to opportunity.trainingType.name,
                "startYear" to opportunity.startYear,
                "startDate" to opportunity.startDate,
                "applicationDeadline" to opportunity.applicationDeadline,
                "salary" to opportunity.salary,
                "languageRequirement" to opportunity.languageRequirement,
                "qualifications" to opportunity.qualifications,
                "description" to opportunity.description,
                "applicationUrl" to opportunity.applicationUrl,
                "companyWebsite" to opportunity.companyWebsite,
                "sourceUrl" to opportunity.sourceUrl,
                "verificationStatus" to opportunity.verificationStatus.name,
                "firstDiscoveredAt" to opportunity.firstDiscoveredAt,
                "lastVerifiedAt" to opportunity.lastVerifiedAt,
                "isSaved" to opportunity.isSaved,
                "applicationStatus" to opportunity.applicationStatus.name,
                "latitude" to opportunity.latitude,
                "longitude" to opportunity.longitude,
                "userId" to uid
            )

            docRef.set(data, SetOptions.merge()).await()
            Log.d("FirestoreSyncService", "Synced opportunity: ${opportunity.id}")
        } catch (e: Exception) {
            Log.w("FirestoreSyncService", "Failed to sync opportunity: ${e.message}")
        }
    }

    suspend fun syncApplication(application: ApplicationEntry) {
        val uid = currentUserId() ?: return
        try {
            val docRef = db.collection("users")
                .document(uid)
                .collection("applications")
                .document(application.id)

            val data = mapOf(
                "opportunityId" to application.opportunityId,
                "companyName" to application.companyName,
                "positionTitle" to application.positionTitle,
                "city" to application.city,
                "bundesland" to application.bundesland,
                "status" to application.status.name,
                "applicationDate" to application.applicationDate,
                "followUpDate" to application.followUpDate,
                "interviewDate" to application.interviewDate,
                "contactPerson" to application.contactPerson,
                "contactEmail" to application.contactEmail,
                "notes" to application.notes,
                "userId" to uid
            )

            docRef.set(data, SetOptions.merge()).await()
            Log.d("FirestoreSyncService", "Synced application: ${application.id}")
        } catch (e: Exception) {
            Log.w("FirestoreSyncService", "Failed to sync application: ${e.message}")
        }
    }

    suspend fun syncScanLog(log: ScanLog) {
        val uid = currentUserId() ?: return
        try {
            val docRef = db.collection("users")
                .document(uid)
                .collection("scan_logs")
                .document(log.id)

            val data = mapOf(
                "timestamp" to log.timestamp,
                "citiesCovered" to log.citiesCovered,
                "companiesDiscovered" to log.companiesDiscovered,
                "opportunitiesVerified" to log.opportunitiesVerified,
                "durationSeconds" to log.durationSeconds,
                "status" to log.status,
                "details" to log.details,
                "userId" to uid
            )

            docRef.set(data, SetOptions.merge()).await()
            Log.d("FirestoreSyncService", "Synced scan log: ${log.id}")
        } catch (e: Exception) {
            Log.w("FirestoreSyncService", "Failed to sync scan log: ${e.message}")
        }
    }
}
