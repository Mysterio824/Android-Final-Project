package com.androidfinalproject.hacktok.model.enums

import com.androidfinalproject.hacktok.model.enums.ReportType

enum class ReportCause(val description: String, val applicableTypes: List<ReportType>) {
    SPAM("Spam or misleading", listOf(ReportType.Post, ReportType.Comment)),
    HARASSMENT("Harassment or bullying", listOf(ReportType.Post, ReportType.Comment, ReportType.User)),
    HATE_SPEECH("Hate speech", listOf(ReportType.Post, ReportType.Comment, ReportType.User)),
    NUDITY("Nudity or sexual activity", listOf(ReportType.Post, ReportType.User)),
    VIOLENCE("Violence or dangerous organizations", listOf(ReportType.Post, ReportType.User)),
    SCAM("Scam or fraud", listOf(ReportType.Post, ReportType.Comment, ReportType.User)),
    INTELLECTUAL_PROPERTY("Intellectual property violation", listOf(ReportType.Post)),
    OTHER("Other", listOf(ReportType.Post, ReportType.Comment, ReportType.User));

    companion object {
        fun fromDescription(description: String): ReportCause? {
            return entries.find { it.description == description }
        }
    }
}