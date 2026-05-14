package ai.nexconn.call.sample.ui.call.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ai.nexconn.call.sample.R

/**
 * Dialog for inviting users to a call
 */
@Composable
fun InviteUserDialog(
    inviteUserIds: String,
    onUserIdsChanged: (String) -> Unit,
    onInvite: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(stringResource(R.string.multi_call_invite_dialog_title))
        },
        text = {
            OutlinedTextField(
                value = inviteUserIds,
                onValueChange = onUserIdsChanged,
                label = { Text(stringResource(R.string.multi_call_invite_dialog_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = onInvite) {
                Text(stringResource(R.string.multi_call_invite_confirm))
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(R.string.multi_call_invite_cancel))
            }
        }
    )
}
