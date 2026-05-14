package ai.nexconn.call.sample.ui.call.single

import android.content.Context
import ai.nexconn.call.sample.R
import ai.nexconn.call.sample.data.model.CallLogItem
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
 * ViewModel for Single Call screen
 * Manages 1v1 call functionality, device controls, and event handling
 */
class SingleCallViewModel(context: Context) : CallViewModel(context) {

    private val _uiState = MutableStateFlow(SingleCallUiState())
    val uiState: StateFlow<SingleCallUiState> = _uiState.asStateFlow()

    /**
     * Called when permissions are granted
     */
    fun onPermissionsGranted() {
        // Permissions granted, ready to make calls
    }

    /**
     * Update callee user ID input
     */
    fun onCalleeUserIdChanged(userId: String) {
        _uiState.update { it.copy(calleeUserId = userId) }
    }

    /**
     * Update selected media type
     */
    fun onMediaTypeChanged(mediaType: NCCallMediaType) {
        _uiState.update { it.copy(selectedMediaType = mediaType) }
    }

    /**
     * Start a 1v1 call
     */
    fun startCall() {
        val calleeUserId = _uiState.value.calleeUserId.trim()

        if (calleeUserId.isEmpty()) {
            ToastUtils.showToast(context, R.string.error_empty_callee_id)
            return
        }

        // Check permissions before starting call
        if (!PermissionUtils.areCallPermissionsGranted(context)) {
            ToastUtils.showToast(context, R.string.error_permissions_denied)
            return
        }

        val params = NCCallStartCallParams(
            listOf(calleeUserId),
            NCCallType.SINGLE,
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

    /**
     * Request to change media type to video
     */
    fun requestChangeToVideo() {
        val params = NCCallRequestChangeMediaTypeParams(NCCallMediaType.AUDIO_VIDEO)
        callEngine.requestChangeMediaType(params)
    }

    /**
     * Reply to media type change request
     */
    fun replyMediaTypeChange(isAgreed: Boolean) {
        val transactionId = _uiState.value.mediaChangeTransactionId ?: return

        val params = NCCallReplyChangeMediaTypeParams(transactionId, isAgreed)
        callEngine.replyChangeMediaType(params)

        _uiState.update {
            it.copy(
                showMediaChangeDialog = false,
                mediaChangeTransactionId = null,
                requestedMediaType = null
            )
        }
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
                        currentCallId = callSession.callId,
                        remoteUserId = if (callSession.remoteParticipants != null) {
                            callSession.remoteParticipants.firstOrNull()?.userId
                        } else {
                            null
                        }
                    )
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

            override fun onRemoteUserInvited(event: NCCallRemoteUserInvitedEvent?) {
                event?.let {
                    ToastUtils.showToast(context, R.string.toast_remote_user_invited, "User invited")
                }
            }

            override fun onCallEnded(event: NCCallEndedEvent) {
                ToastUtils.showToast(context, R.string.toast_call_ended)
                callEngine.enableCamera(false)

                _uiState.update {
                    it.copy(
                        isInCall = false,
                        currentCallId = null,
                        remoteUserId = null,
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

                // Update remote user ID when user joins the call
                if (event.userState == NCCallUserState.ONCALL) {
                    _uiState.update {
                        it.copy(remoteUserId = event.userId)
                    }
                }
            }

            override fun onMediaTypeChangeRequestReceived(event: NCCallMediaTypeChangeRequestReceivedEvent) {
                ToastUtils.showToast(context, R.string.toast_media_type_change_request)

                _uiState.update {
                    it.copy(
                        showMediaChangeDialog = true,
                        mediaChangeTransactionId = event.transactionId,
                        requestedMediaType = event.mediaType
                    )
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

            override fun onRemoteMicrophoneStateChanged(event: NCCallRemoteMicrophoneStateChangedEvent?) {
                event?.let {
                    ToastUtils.showToast(context, R.string.toast_remote_microphone_state_changed, "State changed")
                }
            }

            override fun onRemoteCameraStateChanged(event: NCCallRemoteCameraStateChangedEvent?) {
                event?.let {
                    ToastUtils.showToast(context, R.string.toast_remote_camera_state_changed, "State changed")
                }
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
