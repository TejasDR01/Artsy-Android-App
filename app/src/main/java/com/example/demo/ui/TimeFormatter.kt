//TimeFormatter.kt
package com.example.demo.util

import java.util.Date
import java.util.concurrent.TimeUnit

object TimeFormatter {
    /**
     * Formats the time difference between the current time and the given timestamp
     * into a human-readable string like "24 seconds ago", "1 minute ago", etc.
     */
    fun getTimeAgo(timestamp: Date): String {
        val now = Date().time
        val diff = now - timestamp.time

        return when {
            // Less than a minute
            diff < TimeUnit.MINUTES.toMillis(1) -> {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
                "$seconds seconds ago"
            }
            // Less than 2 minutes
            diff < TimeUnit.MINUTES.toMillis(2) -> {
                "a minute ago"
            }
            // Less than an hour
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes minutes ago"
            }
            // Less than 2 hours
            diff < TimeUnit.HOURS.toMillis(2) -> {
                "an hour ago"
            }
            // Less than a day
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours hours ago"
            }
            // Less than 2 days
            diff < TimeUnit.DAYS.toMillis(2) -> {
                "a day ago"
            }
            // Less than a week
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days days ago"
            }
            // Less than 2 weeks
            diff < TimeUnit.DAYS.toMillis(14) -> {
                "a week ago"
            }
            // Less than a month
            diff < TimeUnit.DAYS.toMillis(30) -> {
                val weeks = TimeUnit.MILLISECONDS.toDays(diff) / 7
                "$weeks weeks ago"
            }
            // Less than 2 months
            diff < TimeUnit.DAYS.toMillis(60) -> {
                "a month ago"
            }
            // Less than a year
            diff < TimeUnit.DAYS.toMillis(365) -> {
                val months = TimeUnit.MILLISECONDS.toDays(diff) / 30
                "$months months ago"
            }
            // Less than 2 years
            diff < TimeUnit.DAYS.toMillis(730) -> {
                "a year ago"
            }
            // More than 2 years
            else -> {
                val years = TimeUnit.MILLISECONDS.toDays(diff) / 365
                "$years years ago"
            }
        }
    }

    fun needsRealTimeUpdates(timestamp: Date): Boolean {
        val now = Date().time
        val diff = now - timestamp.time
        return diff < TimeUnit.MINUTES.toMillis(2)
    }
}
