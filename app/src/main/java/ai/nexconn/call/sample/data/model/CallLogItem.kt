package ai.nexconn.call.sample.data.model

/**
 * Data model for call log items
 */
data class CallLogItem(
    val startTime: Long,
    val endTime: Long,
    val participants: List<String>,
    val endReason: String
)
