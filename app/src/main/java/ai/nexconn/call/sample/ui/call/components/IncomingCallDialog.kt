package ai.nexconn.call.sample.ui.call.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ai.nexconn.call.sample.R

/**
 * Dialog for incoming call
 * Shows caller information and accept/reject options
 */
@Composable
fun IncomingCallDialog(
    callerUserId: String,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Cannot dismiss by clicking outside */ },
        title = {
            Text(stringResource(R.string.dialog_incoming_call_title))
        },
        text = {
            Text(stringResource(R.string.dialog_incoming_call_message, callerUserId))
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text(stringResource(R.string.dialog_accept_call))
            }
        },
        dismissButton = {
            Button(
                onClick = onReject,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.dialog_reject_call))
            }
        }
    )
}
