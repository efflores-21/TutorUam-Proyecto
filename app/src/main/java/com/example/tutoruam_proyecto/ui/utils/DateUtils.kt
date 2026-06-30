package com.example.tutoruam_proyecto.ui.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatDate(isoDate: String?): String {
        if (isoDate == null) return ""
        val date = parseIso(isoDate)
        return if (date != null) {
            SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(date)
        } else {
            isoDate
        }
    }

    fun formatTime(isoDate: String?): String {
        if (isoDate == null) return ""
        val date = parseIso(isoDate)
        return if (date != null) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        } else {
            isoDate
        }
    }

    private fun parseIso(isoDate: String): Date? {
        val formats = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                // Si contiene 'T', aseguramos que el formato lo use
                return sdf.parse(isoDate)
            } catch (e: Exception) {
                // Continue
            }
        }
        return null
    }
}
