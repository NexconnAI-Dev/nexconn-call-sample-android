package ai.nexconn.call.sample.ui.call.multi

import ai.nexconn.call.sample.data.model.CallLogItem
import ai.nexconn.call.sample.data.model.UserVideoItem
import ai.nexconn.call.api.model.NCCallMediaType

/**
 * UI state for the Multi Call screen
 */
data class MultiCallUiState(
    // Input fields
    val calleeUserIds: String = "",
    val selectedMediaType: NCCallMediaType = NCCallMediaType.AUDIO_VIDEO,

    // Call state
    val isInCall: Boolean = false,
    val currentCallId: String? = null,

    // Device state
    val isCameraEnabled: Boolean = false,
    val isMicrophoneEnabled: Boolean = true,
    val isSpeakerEnabled: Boolean = true,

    // User list
    val userList: List<UserVideoItem> = emptyList(),

    // Dialog state
    val showIncomingCallDialog: Boolean = false,
    val incomingCallId: String? = null,
    val incomingCallerUserId: String? = null,

    val showInviteDialog: Boolean = false,
    val inviteUserIds: String = "",

    // Call logs
    val callLogs: List<CallLogItem> = emptyList()
)
