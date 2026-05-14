package ai.nexconn.call.sample.ui.call.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ai.nexconn.call.sample.R

/**
 * Dialog for media type change request
 * Shows the requested media type and agree/disagree options
 */
@Composable
fun MediaTypeChangeDialog(
    requestedMediaType: String,
    onAgree: () -> Unit,
    onDisagree: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Cannot dismiss by clicking outside */ },
        title = {
            Text(stringResource(R.string.dialog_media_change_title))
        },
        text = {
            Text(stringResource(R.string.dialog_media_change_message, requestedMediaType))
        },
        confirmButton = {
            Button(onClick = onAgree) {
                Text(stringResource(R.string.dialog_agree))
            }
        },
        dismissButton = {
            Button(
                onClick = onDisagree,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.dialog_disagree))
            }
        }
    )
}
