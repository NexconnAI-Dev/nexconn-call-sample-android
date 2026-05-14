package ai.nexconn.call.sample.ui.call.single

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ai.nexconn.call.sample.R
import ai.nexconn.call.sample.ui.call.components.*
import ai.nexconn.call.sample.utils.PermissionUtils
import ai.nexconn.call.sample.utils.ToastUtils
import ai.nexconn.call.api.model.NCCallMediaType

/**
 * Single Call screen for 1v1 calls
 * Implements pin-shaped layout with controls on left, video on right, and history at bottom
 */
@Composable
fun SingleCallScreen() {
    val context = LocalContext.current
    val viewModel = remember { SingleCallViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.onPermissionsGranted()
        } else {
            ToastUtils.showToast(context, R.string.error_permissions_denied)
        }
    }

    // Register handlers when entering the screen
    DisposableEffect(Unit) {
        viewModel.registerHandlers()

        // Request permissions on screen entry
        if (!PermissionUtils.areCallPermissionsGranted(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }

        onDispose {
            viewModel.unregisterHandlers()
        }
    }

    // Show incoming call dialog
    if (uiState.showIncomingCallDialog && uiState.incomingCallerUserId != null) {
        IncomingCallDialog(
            callerUserId = uiState.incomingCallerUserId!!,
            onAccept = viewModel::acceptCall,
            onReject = viewModel::rejectCall
        )
    }

    // Show media type change dialog
    if (uiState.showMediaChangeDialog && uiState.requestedMediaType != null) {
        MediaTypeChangeDialog(
            requestedMediaType = uiState.requestedMediaType.toString(),
            onAgree = { viewModel.replyMediaTypeChange(true) },
            onDisagree = { viewModel.replyMediaTypeChange(false) }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Pin-shaped layout: Left controls + Right video
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left side: Controls
                ControlSection(
                    uiState = uiState,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )

                // Right side: Video views
                VideoSection(
                    uiState = uiState,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom: Call history
            CallHistoryList(
                callLogs = uiState.callLogs,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

/**
 * Control section on the left side
 */
@Composable
private fun ControlSection(
    uiState: SingleCallUiState,
    viewModel: SingleCallViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Callee User ID input
        OutlinedTextField(
            value = uiState.calleeUserId,
            onValueChange = viewModel::onCalleeUserIdChanged,
            label = { Text(stringResource(R.string.single_call_callee_hint)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isInCall
        )

        // Media Type selection
        Text(
            text = stringResource(R.string.single_call_media_type_label),
            style = MaterialTheme.typography.labelMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.selectedMediaType == NCCallMediaType.AUDIO_VIDEO,
                onClick = { viewModel.onMediaTypeChanged(NCCallMediaType.AUDIO_VIDEO) },
                label = { Text(stringResource(R.string.single_call_audio_video)) },
                enabled = !uiState.isInCall
            )
            FilterChip(
                selected = uiState.selectedMediaType == NCCallMediaType.AUDIO,
                onClick = { viewModel.onMediaTypeChanged(NCCallMediaType.AUDIO) },
                label = { Text(stringResource(R.string.single_call_audio_only)) },
                enabled = !uiState.isInCall
            )
        }

        // Call control buttons
        Button(
            onClick = viewModel::startCall,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isInCall
        ) {
            Text(stringResource(R.string.single_call_start_button))
        }

        Button(
            onClick = viewModel::endCall,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isInCall,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(stringResource(R.string.single_call_end_button))
        }

        // Device control buttons
        Button(
            onClick = viewModel::toggleCamera,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isInCall
        ) {
            Text(
                stringResource(
                    if (uiState.isCameraEnabled) {
                        R.string.single_call_disable_camera
                    } else {
                        R.string.single_call_enable_camera
                    }
                )
            )
        }

        Button(
            onClick = viewModel::switchCamera,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isInCall && uiState.isCameraEnabled
        ) {
            Text(stringResource(R.string.single_call_switch_camera))
        }

        Button(
            onClick = viewModel::toggleMicrophone,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isInCall
        ) {
            Text(
                stringResource(
                    if (uiState.isMicrophoneEnabled) {
                        R.string.single_call_disable_microphone
                    } else {
                        R.string.single_call_enable_microphone
                    }
                )
            )
        }

        Button(
            onClick = viewModel::toggleSpeaker,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isInCall
        ) {
            Text(
                stringResource(
                    if (uiState.isSpeakerEnabled) {
                        R.string.single_call_disable_speaker
                    } else {
                        R.string.single_call_enable_speaker
                    }
                )
            )
        }

        Button(
            onClick = viewModel::requestChangeToVideo,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isInCall && uiState.selectedMediaType == NCCallMediaType.AUDIO
        ) {
            Text(stringResource(R.string.single_call_change_to_video))
        }
    }
}

/**
 * Video section on the right side
 */
@Composable
private fun VideoSection(
    uiState: SingleCallUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Local user video
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.single_call_local_user),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LocalVideoView(
                isVideoEnabled = uiState.isInCall &&
                        uiState.selectedMediaType == NCCallMediaType.AUDIO_VIDEO &&
                        uiState.isCameraEnabled,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Remote user video
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.single_call_remote_user),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            RemoteVideoView(
                userId = uiState.remoteUserId,
                isVideoEnabled = uiState.isInCall &&
                        uiState.selectedMediaType == NCCallMediaType.AUDIO_VIDEO,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
