package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Report
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReportRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("reports")

    // Thêm báo cáo mới
    suspend fun addReport(report: Report): String {
        val documentRef = collection.add(report).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy báo cáo theo ID
    suspend fun getReport(reportId: String): Report? {
        val snapshot = collection.document(reportId).get().await()
        return snapshot.toObject(Report::class.java)
    }

    // Lấy danh sách báo cáo chưa xử lý
    suspend fun getPendingReports(): List<Report> {
        val snapshot = collection.whereEqualTo("status", "pending").get().await()
        return snapshot.toObjects(Report::class.java)
    }

    // Cập nhật trạng thái báo cáo
    suspend fun updateReport(reportId: String, updates: Map<String, Any>) {
        collection.document(reportId).update(updates).await()
    }

    // Xóa báo cáo
    suspend fun deleteReport(reportId: String) {
        collection.document(reportId).delete().await()
    }
}