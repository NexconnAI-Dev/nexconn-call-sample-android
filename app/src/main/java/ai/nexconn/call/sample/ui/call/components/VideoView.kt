package ai.nexconn.call.sample.ui.call.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ai.nexconn.call.sample.R
import ai.nexconn.call.api.NCCallEngine
import ai.nexconn.call.api.model.NCCallRenderMode
import ai.nexconn.call.api.view.NCCallLocalVideoSurfaceView
import ai.nexconn.call.api.view.NCCallRemoteVideoSurfaceView

/**
 * Composable for displaying local video view
 */
@Composable
fun LocalVideoView(
    isVideoEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (isVideoEnabled) {
            val context = LocalContext.current
            val callEngine = NCCallEngine.getInstance()

            AndroidView(
                factory = { ctx ->
                    NCCallLocalVideoSurfaceView(ctx).apply {
                        setRenderMode(NCCallRenderMode.FILL)
                        callEngine.setLocalVideoView(this)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = stringResource(R.string.single_call_audio_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Composable for displaying remote video view
 */
@Composable
fun RemoteVideoView(
    userId: String?,
    isVideoEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (isVideoEnabled && userId != null) {
            val context = LocalContext.current
            val callEngine = NCCallEngine.getInstance()

            // Use key to force recomposition when userId changes
            key(userId) {
                AndroidView(
                    factory = { ctx ->
                        NCCallRemoteVideoSurfaceView(ctx, userId, false).apply {
                            setRenderMode(NCCallRenderMode.FILL)
                            callEngine.setRemoteVideoView(listOf(this))
                        }
                    },
                    update = { view ->
                        // Update the view when userId changes
                        callEngine.setRemoteVideoView(listOf(view))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Text(
                text = if (userId != null) {
                    stringResource(R.string.single_call_remote_audio_label, userId)
                } else {
                    stringResource(R.string.single_call_remote_user)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
