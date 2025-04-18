package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Report
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    /**
     * Adds a new report to the database.
     * The implementation should handle setting the document ID on the report object.
     * @param report The report object to add.
     * @return The ID of the newly created report.
     * @throws Exception if the operation fails.
     */
    suspend fun addReport(report: Report): String

    /**
     * Retrieves a report by its ID.
     * @param reportId The ID of the report to retrieve.
     * @return The Report object, or null if not found.
     * @throws Exception if the operation fails.
     */
    suspend fun getReport(reportId: String): Report?

    /**
     * Retrieves a list of reports with a 'pending' status.
     * @return A list of pending Report objects.
     * @throws Exception if the operation fails.
     */
    suspend fun getPendingReports(): List<Report>

    /**
     * Updates specific fields of a report.
     * @param reportId The ID of the report to update.
     * @param updates A map of field names to their new values.
     * @throws Exception if the operation fails.
     */
    suspend fun updateReport(reportId: String, updates: Map<String, Any>)

    /**
     * Deletes a report by its ID.
     * @param reportId The ID of the report to delete.
     * @throws Exception if the operation fails.
     */
    suspend fun deleteReport(reportId: String)

    /**
     * Observes the list of pending reports in real-time.
     * Emits a new list whenever the pending reports change in the database.
     * @return A Flow emitting lists of pending Report objects.
     */
    fun getPendingReportsFlow(): Flow<List<Report>>
}