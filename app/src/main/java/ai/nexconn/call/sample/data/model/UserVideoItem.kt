package ai.nexconn.call.sample.data.model

/**
 * Data model for user video items in multi-call
 */
data class UserVideoItem(
    val userId: String,
    val isLocal: Boolean = false,
    val isVideoEnabled: Boolean = true
)
