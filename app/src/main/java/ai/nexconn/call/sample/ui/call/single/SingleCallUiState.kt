package ai.nexconn.call.sample.ui.call.single

import ai.nexconn.call.api.model.NCCallMediaType

/**
 * UI state for the Single Call screen
 */
data class SingleCallUiState(
    // Input fields
    val calleeUserId: String = "",
    val selectedMediaType: NCCallMediaType = NCCallMediaType.AUDIO_VIDEO,

    // Call state
    val isInCall: Boolean = false,
    val currentCallId: String? = null,
    val remoteUserId: String? = null,

    // Device state
    val isCameraEnabled: Boolean = false,
    val isMicrophoneEnabled: Boolean = true,
    val isSpeakerEnabled: Boolean = true,

    // Dialog state
    val showIncomingCallDialog: Boolean = false,
    val incomingCallId: String? = null,
    val incomingCallerUserId: String? = null,

    val showMediaChangeDialog: Boolean = false,
    val mediaChangeTransactionId: String? = null,
    val requestedMediaType: NCCallMediaType? = null,

    // Call logs
    val callLogs: List<ai.nexconn.call.sample.data.model.CallLogItem> = emptyList()
)
