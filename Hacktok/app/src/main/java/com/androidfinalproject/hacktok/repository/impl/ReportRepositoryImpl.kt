package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.repository.ReportRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReportRepository {

    private val collection = db.collection("reports")
    private val TAG = "ReportRepositoryImpl"

    override suspend fun addReport(report: Report): String {
        return try {
            val documentRef = collection.add(report).await()
            // Update the report object with the generated ID
            val reportId = documentRef.id
            collection.document(reportId).update("id", reportId).await()
            reportId
        } catch (e: Exception) {
            throw Exception("Failed to add report: ${e.message}", e)
        }
    }

    override suspend fun getReport(reportId: String): Report? {
        return try {
            val snapshot = collection.document(reportId).get().await()
            snapshot.toObject(Report::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to get report $reportId: ${e.message}", e)
        }
    }

    override suspend fun getPendingReports(): List<Report> {
        return try {
            val snapshot = collection.whereEqualTo("status", "pending").get().await()
            // Ensure correct deserialization with potential nulls handled by model
            snapshot.toObjects(Report::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to get pending reports: ${e.message}", e)
        }
    }

    override suspend fun updateReport(reportId: String, updates: Map<String, Any>) {
        try {
            collection.document(reportId).update(updates).await()
        } catch (e: Exception) {
            throw Exception("Failed to update report $reportId: ${e.message}", e)
        }
    }

    override suspend fun deleteReport(reportId: String) {
        try {
            collection.document(reportId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete report $reportId: ${e.message}", e)
        }
    }

    override fun getPendingReportsFlow(): Flow<List<Report>> {
        val query = collection.whereEqualTo("status", "pending")

        return query.snapshots()
            .map { snapshot ->
                snapshot.toObjects<Report>()
            }
            .catch { exception ->
                Log.e(TAG, "Error getting pending reports flow", exception)
                emit(emptyList())
            }
    }

    override suspend fun getReportsForUser(userId: String): List<Report> {
        val query = collection
            .whereEqualTo("targetId", userId)
            .whereEqualTo("status", "pending")

        return try {
            val snapshot = query.get().await()
            snapshot.toObjects(Report::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to get pending reports for user $userId: ${e.message}", e)
        }
    }
} 