package ai.nexconn.call.sample.ui.call.multi

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ai.nexconn.call.sample.R
import ai.nexconn.call.sample.data.model.UserVideoItem
import ai.nexconn.call.sample.ui.call.components.*
import ai.nexconn.call.sample.utils.PermissionUtils
import ai.nexconn.call.sample.utils.ToastUtils
import ai.nexconn.call.api.model.NCCallMediaType

/**
 * Multi Call screen for group calls
 * Implements pin-shaped layout with controls on left, user list on right, and history at bottom
 */
@Composable
fun MultiCallScreen() {
    val context = LocalContext.current
    val viewModel = remember { MultiCallViewModel(context) }
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

    // Show invite dialog
    if (uiState.showInviteDialog) {
        InviteUserDialog(
            inviteUserIds = uiState.inviteUserIds,
            onUserIdsChanged = viewModel::onInviteUserIdsChanged,
            onInvite = viewModel::inviteToCall,
            onCancel = viewModel::hideInviteDialog
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
            // Pin-shaped layout: Left controls + Right user list
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

                // Right side: User list
                UserListSection(
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
    uiState: MultiCallUiState,
    viewModel: MultiCallViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Callee User IDs input
        OutlinedTextField(
            value = uiState.calleeUserIds,
            onValueChange = viewModel::onCalleeUserIdsChanged,
            label = { Text(stringResource(R.string.multi_call_callees_hint)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isInCall,
            minLines = 2,
            maxLines = 3
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
            onClick = viewModel::showInviteDialog,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isInCall
        ) {
            Text(stringResource(R.string.multi_call_invite_button))
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
    }
}

/**
 * User list section on the right side
 */
@Composable
private fun UserListSection(
    uiState: MultiCallUiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.multi_call_user_list_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.userList) { user ->
                UserVideoItemView(
                    user = user,
                    isVideoCall = uiState.selectedMediaType == NCCallMediaType.AUDIO_VIDEO
                )
            }
        }
    }
}

/**
 * Individual user video item view
 */
@Composable
private fun UserVideoItemView(
    user: UserVideoItem,
    isVideoCall: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isVideoCall && user.isVideoEnabled) {
                if (user.isLocal) {
                    LocalVideoView(
                        isVideoEnabled = true,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    RemoteVideoView(
                        userId = user.userId,
                        isVideoEnabled = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Show text for audio mode
                Text(
                    text = if (user.isLocal) {
                        stringResource(R.string.single_call_audio_label)
                    } else {
                        stringResource(R.string.single_call_remote_audio_label, user.userId)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
