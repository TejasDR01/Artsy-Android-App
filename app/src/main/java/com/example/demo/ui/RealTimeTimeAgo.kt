package com.example.demo.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.demo.util.TimeFormatter
import kotlinx.coroutines.delay
import java.util.Date

/**
 * A composable that displays a time-ago string that updates in real-time,
 * especially useful for recently added items where you want to see the
 * seconds counting up (1s ago, 2s ago, etc.)
 */
@Composable
fun RealTimeTimeAgo(
    timestamp: Date,
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    fontSize: Int = 14
) {
    // State to trigger recomposition
    var timeAgoText by remember { mutableStateOf(TimeFormatter.getTimeAgo(timestamp)) }

    // Check if this timestamp needs real-time updates (recent timestamps)
    val needsRealTimeUpdates = remember(timestamp) {
        TimeFormatter.needsRealTimeUpdates(timestamp)
    }

    // Effect to update the time text periodically for recent timestamps
    LaunchedEffect(timestamp) {
        if (needsRealTimeUpdates) {
            while (true) {
                timeAgoText = TimeFormatter.getTimeAgo(timestamp)
                // Update more frequently for very recent items
                delay(1000) // Update every second

                // If it's no longer recent enough for real-time updates, we can slow down
                if (!TimeFormatter.needsRealTimeUpdates(timestamp)) {
                    break
                }
            }
        }
    }

    // If we're not in real-time update mode anymore, still update periodically
    // but much less frequently
    LaunchedEffect(needsRealTimeUpdates) {
        if (!needsRealTimeUpdates) {
            while (true) {
                timeAgoText = TimeFormatter.getTimeAgo(timestamp)
                delay(60000) // Update every minute for older timestamps
            }
        }
    }

    Text(
        text = timeAgoText,
        fontSize = fontSize.sp,
        color = color,
        modifier = modifier
    )
}