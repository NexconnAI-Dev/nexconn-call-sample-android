package ai.nexconn.call.sample.ui.call.multi

import android.content.Context
import ai.nexconn.call.sample.R
import ai.nexconn.call.sample.data.model.CallLogItem
import ai.nexconn.call.sample.data.model.UserVideoItem
import ai.nexconn.call.sample.ui.call.CallViewModel
import ai.nexconn.call.sample.utils.PermissionUtils
import ai.nexconn.call.sample.utils.ToastUtils
import ai.nexconn.call.api.handler.*
import ai.nexconn.call.api.handler.result.*
import ai.nexconn.call.api.handler.event.*
import ai.nexconn.call.api.model.*
import ai.nexconn.call.api.params.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for Multi Call screen
 * Manages group call functionality, user list, and event handling
 */
class MultiCallViewModel(context: Context) : CallViewModel(context) {

    private val _uiState = MutableStateFlow(MultiCallUiState())
    val uiState: StateFlow<MultiCallUiState> = _uiState.asStateFlow()

    /**
     * Called when permissions are granted
     */
    fun onPermissionsGranted() {
        // Permissions granted, ready to make calls
    }

    /**
     * Update callee user IDs input
     */
    fun onCalleeUserIdsChanged(userIds: String) {
        _uiState.update { it.copy(calleeUserIds = userIds) }
    }

    /**
     * Update selected media type
     */
    fun onMediaTypeChanged(mediaType: NCCallMediaType) {
        _uiState.update { it.copy(selectedMediaType = mediaType) }
    }

    /**
     * Start a multi-user call
     */
    fun startCall() {
        val calleeUserIds = _uiState.value.calleeUserIds.trim()

        if (calleeUserIds.isEmpty()) {
            ToastUtils.showToast(context, R.string.error_empty_callee_id)
            return
        }

        // Check permissions before starting call
        if (!PermissionUtils.areCallPermissionsGranted(context)) {
            ToastUtils.showToast(context, R.string.error_permissions_denied)
            return
        }

        // Parse comma-separated user IDs
        val userIdList = calleeUserIds.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (userIdList.isEmpty()) {
            ToastUtils.showToast(context, R.string.error_empty_callee_id)
            return
        }

        val params = NCCallStartCallParams(
            userIdList,
            NCCallType.MULTI,
            _uiState.value.selectedMediaType
        )

        callEngine.startCall(params)
    }

    /**
     * Accept incoming call
     */
    fun acceptCall() {
        val callId = _uiState.value.incomingCallId ?: return

        // Check permissions before accepting call
        if (!PermissionUtils.areCallPermissionsGranted(context)) {
            ToastUtils.showToast(context, R.string.error_permissions_denied)
            return
        }

        val params = NCCallAcceptCallParams(callId)
        callEngine.acceptCall(params)

        _uiState.update {
            it.copy(
                showIncomingCallDialog = false,
                incomingCallId = null,
                incomingCallerUserId = null
            )
        }
    }

    /**
     * Reject incoming call
     */
    fun rejectCall() {
        val callId = _uiState.value.incomingCallId ?: return

        val params = NCCallEndCallParams(callId)
        callEngine.endCall(params)

        _uiState.update {
            it.copy(
                showIncomingCallDialog = false,
                incomingCallId = null,
                incomingCallerUserId = null
            )
        }
    }

    /**
     * End current call
     */
    fun endCall() {
        val callId = _uiState.value.currentCallId ?: return

        val params = NCCallEndCallParams(callId)
        callEngine.endCall(params)
    }

    /**
     * Show invite dialog
     */
    fun showInviteDialog() {
        _uiState.update { it.copy(showInviteDialog = true) }
    }

    /**
     * Hide invite dialog
     */
    fun hideInviteDialog() {
        _uiState.update { it.copy(showInviteDialog = false, inviteUserIds = "") }
    }

    /**
     * Update invite user IDs input
     */
    fun onInviteUserIdsChanged(userIds: String) {
        _uiState.update { it.copy(inviteUserIds = userIds) }
    }

    /**
     * Invite users to current call
     */
    fun inviteToCall() {
        val inviteUserIds = _uiState.value.inviteUserIds.trim()

        if (inviteUserIds.isEmpty()) {
            ToastUtils.showToast(context, R.string.error_empty_callee_id)
            return
        }

        // Parse comma-separated user IDs
        val userIdList = inviteUserIds.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (userIdList.isEmpty()) {
            ToastUtils.showToast(context, R.string.error_empty_callee_id)
            return
        }

        val params = NCCallInviteToCallParams(userIdList)
        callEngine.inviteToCall(params)

        hideInviteDialog()
    }

    /**
     * Toggle camera on/off
     */
    fun toggleCamera() {
        val newState = !_uiState.value.isCameraEnabled
        callEngine.enableCamera(newState)
        _uiState.update { it.copy(isCameraEnabled = newState) }
    }

    /**
     * Switch between front and back camera
     */
    fun switchCamera() {
        callEngine.switchCamera()
    }

    /**
     * Toggle microphone on/off
     */
    fun toggleMicrophone() {
        val newState = !_uiState.value.isMicrophoneEnabled
        callEngine.enableMicrophone(newState)
        _uiState.update { it.copy(isMicrophoneEnabled = newState) }
    }

    /**
     * Toggle speaker on/off
     */
    fun toggleSpeaker() {
        val newState = !_uiState.value.isSpeakerEnabled
        callEngine.enableSpeaker(newState)
        _uiState.update { it.copy(isSpeakerEnabled = newState) }
    }

    override fun createEventHandler(): NCCallEventHandler {
        return object : NCCallEventHandler {
            override fun onCallReceived(event: NCCallReceivedEvent) {
                ToastUtils.showToast(context, R.string.toast_call_received)

                val callSession = event.callSession
                _uiState.update {
                    it.copy(
                        showIncomingCallDialog = true,
                        incomingCallId = callSession.callId,
                        incomingCallerUserId = callSession.callerUserId
                    )
                }
            }

            override fun onCallConnected(event: NCCallConnectedEvent) {
                ToastUtils.showToast(context, R.string.toast_call_connected)

                val callSession = event.callSession
                _uiState.update {
                    it.copy(
                        isInCall = true,
                        currentCallId = callSession.callId
                    )
                }

                // Add local user to the list
                val localUser = UserVideoItem(
                    userId = "Local",
                    isLocal = true,
                    isVideoEnabled = callSession.mediaType == NCCallMediaType.AUDIO_VIDEO
                )

                // Add existing remote users to the list
                val remoteUsers = callSession.remoteParticipants.map { participant ->
                    UserVideoItem(
                        userId = participant.userId,
                        isLocal = false,
                        isVideoEnabled = callSession.mediaType == NCCallMediaType.AUDIO_VIDEO
                    )
                }

                _uiState.update {
                    it.copy(userList = listOf(localUser) + remoteUsers)
                }

                // Enable camera and microphone for video calls
                if (callSession.mediaType == NCCallMediaType.AUDIO_VIDEO) {
                    callEngine.enableCamera(true)
                    _uiState.update { it.copy(isCameraEnabled = true) }
                }

                // Always enable microphone for calls
                callEngine.enableMicrophone(true)
                _uiState.update { it.copy(isMicrophoneEnabled = true) }
            }

            override fun onCallEnded(event: NCCallEndedEvent) {
                ToastUtils.showToast(context, R.string.toast_call_ended)

                _uiState.update {
                    it.copy(
                        isInCall = false,
                        currentCallId = null,
                        userList = emptyList(),
                        isCameraEnabled = false
                    )
                }
            }

            override fun onRemoteUserStateChanged(event: NCCallRemoteUserStateChangedEvent) {
                ToastUtils.showToast(
                    context,
                    R.string.toast_remote_user_state_changed,
                    event.userState.toString()
                )

                val userId = event.userId
                val userState = event.userState

                when (userState) {
                    NCCallUserState.ONCALL -> {
                        // Check if user already exists in the list
                        val userExists = _uiState.value.userList.any { it.userId == userId }
                        if (!userExists) {
                            // Add user to the list
                            val newUser = UserVideoItem(
                                userId = userId,
                                isLocal = false,
                                isVideoEnabled = _uiState.value.selectedMediaType == NCCallMediaType.AUDIO_VIDEO
                            )
                            _uiState.update {
                                it.copy(userList = it.userList + newUser)
                            }
                        }
                    }
                    NCCallUserState.IDLE -> {
                        // Remove user from the list
                        _uiState.update {
                            it.copy(userList = it.userList.filter { user -> user.userId != userId })
                        }
                    }
                    else -> {
                        // Handle other states if needed
                    }
                }
            }

            override fun onServerCallLogReceived(event: NCCallServerCallLogReceivedEvent) {
                ToastUtils.showToast(context, R.string.toast_call_log_received)

                val log = event.callLog
                val logItem = CallLogItem(
                    startTime = log.startTime,
                    endTime = log.endTime,
                    participants = log.participants,
                    endReason = log.endReason.toString()
                )

                _uiState.update {
                    it.copy(callLogs = it.callLogs + logItem)
                }
            }

            override fun onRemoteUserInvited(event: NCCallRemoteUserInvitedEvent) {
                ToastUtils.showToast(context, R.string.toast_remote_user_invited, "User invited")
            }

            override fun onRemoteCameraStateChanged(event: NCCallRemoteCameraStateChangedEvent) {
                ToastUtils.showToast(context, R.string.toast_remote_camera_state_changed, "State changed")
            }

            override fun onMediaTypeChangeRequestReceived(event: NCCallMediaTypeChangeRequestReceivedEvent?) {
                event?.let {
                    ToastUtils.showToast(context, R.string.toast_media_type_change_request)
                }
            }

            override fun onMediaTypeChangeResultReceived(event: NCCallMediaTypeChangeResultReceivedEvent?) {
                event?.let {
                    ToastUtils.showToast(context, R.string.toast_media_type_change_result, "Result received")
                }
            }

            override fun onCallTypeChanged(event: NCCallTypeChangedEvent?) {
                event?.let {
                    ToastUtils.showToast(context, R.string.toast_call_type_changed)
                }
            }

            override fun onLocalCallLogReceived(event: NCCallLocalCallLogReceivedEvent?) {
                event?.let {
                    ToastUtils.showToast(context, R.string.toast_local_call_log_received)
                }
            }

            override fun onStartTimeReceived(event: NCCallStartTimeReceivedEvent?) {
                event?.let {
                    ToastUtils.showToast(context, R.string.toast_start_time_received)
                }
            }

            override fun onRemoteMicrophoneStateChanged(event: NCCallRemoteMicrophoneStateChangedEvent) {
                ToastUtils.showToast(context, R.string.toast_remote_microphone_state_changed, "State changed")
            }
        }
    }

    override fun createResultHandler(): NCCallAPIResultHandler {
        return object : NCCallAPIResultHandler {
            override fun onStartCall(result: NCCallStartCallResult) {
                if (result.code == NCCallCode.SUCCESS) {
                    ToastUtils.showToast(context, R.string.toast_start_call_success)
                } else {
                    ToastUtils.showToast(context, R.string.error_connection_failed)
                }
            }
        }
    }
}
