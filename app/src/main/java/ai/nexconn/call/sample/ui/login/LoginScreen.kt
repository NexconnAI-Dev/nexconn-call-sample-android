package ai.nexconn.call.sample.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ai.nexconn.call.sample.R

/**
 * Login screen for connecting to Nexconn services
 * Shows two different UI states:
 * - Case 1: Input fields for appKey and userToken with Connect button
 * - Case 2: Navigation buttons after successful connection
 */
@Composable
fun LoginScreen(
    onNavigateToSingleCall: () -> Unit,
    onNavigateToMultiCall: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!uiState.isConnected) {
                // Case 1: Login UI
                LoginInputSection(
                    appKey = uiState.appKey,
                    userToken = uiState.userToken,
                    isLoading = uiState.isLoading,
                    onAppKeyChanged = viewModel::onAppKeyChanged,
                    onUserTokenChanged = viewModel::onUserTokenChanged,
                    onConnectClick = viewModel::connect
                )
            } else {
                // Case 2: Navigation UI
                NavigationSection(
                    onDisconnectClick = viewModel::disconnect,
                    onSingleCallClick = onNavigateToSingleCall,
                    onMultiCallClick = onNavigateToMultiCall
                )
            }
        }
    }
}

/**
 * Login input section (Case 1)
 * Shows input fields and connect button
 */
@Composable
private fun LoginInputSection(
    appKey: String,
    userToken: String,
    isLoading: Boolean,
    onAppKeyChanged: (String) -> Unit,
    onUserTokenChanged: (String) -> Unit,
    onConnectClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.app_name),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 32.dp)
    )

    OutlinedTextField(
        value = appKey,
        onValueChange = onAppKeyChanged,
        label = { Text(stringResource(R.string.login_app_key_hint)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        enabled = !isLoading,
        singleLine = true
    )

    OutlinedTextField(
        value = userToken,
        onValueChange = onUserTokenChanged,
        label = { Text(stringResource(R.string.login_user_token_hint)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        enabled = !isLoading,
        singleLine = true
    )

    Button(
        onClick = onConnectClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(stringResource(R.string.login_connect_button))
        }
    }
}

/**
 * Navigation section (Case 2)
 * Shows navigation buttons after successful connection
 */
@Composable
private fun NavigationSection(
    onDisconnectClick: () -> Unit,
    onSingleCallClick: () -> Unit,
    onMultiCallClick: () -> Unit
) {
    Text(
        text = stringResource(R.string.app_name),
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 32.dp)
    )

    Button(
        onClick = onDisconnectClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(bottom = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text(stringResource(R.string.login_disconnect_button))
    }

    Button(
        onClick = onSingleCallClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(bottom = 16.dp)
    ) {
        Text(stringResource(R.string.login_single_call_button))
    }

    Button(
        onClick = onMultiCallClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(stringResource(R.string.login_multi_call_button))
    }
}
