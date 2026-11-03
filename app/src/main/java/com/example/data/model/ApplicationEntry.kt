package com.example.data.model

data class ApplicationEntry(
    val id: String,
    val opportunityId: String,
    val companyName: String,
    val positionTitle: String,
    val city: String,
    val bundesland: String,
    val status: ApplicationStatus,
    val applicationDate: String,
    val followUpDate: String,
    val interviewDate: String,
    val contactPerson: String,
    val contactEmail: String,
    val notes: String
)
