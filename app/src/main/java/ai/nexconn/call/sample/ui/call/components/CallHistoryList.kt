package ai.nexconn.call.sample.ui.call.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ai.nexconn.call.sample.R
import ai.nexconn.call.sample.data.model.CallLogItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable for displaying call history list
 */
@Composable
fun CallHistoryList(
    callLogs: List<CallLogItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.single_call_history_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(callLogs) { log ->
                CallLogItemView(log)
            }
        }
    }
}

/**
 * Individual call log item view
 */
@Composable
private fun CallLogItemView(log: CallLogItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.call_history_start_time,
                    formatTimestamp(log.startTime)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(
                    R.string.call_history_end_time,
                    formatTimestamp(log.endTime)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(
                    R.string.call_history_participants,
                    log.participants.joinToString(", ")
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(
                    R.string.call_history_end_reason,
                    log.endReason
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Format timestamp to readable date/time string
 */
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
