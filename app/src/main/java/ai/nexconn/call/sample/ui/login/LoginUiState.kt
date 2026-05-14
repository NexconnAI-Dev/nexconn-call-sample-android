package ai.nexconn.call.sample.ui.login

/**
 * UI state for the Login screen
 */
data class LoginUiState(
    val appKey: String = "",
    val userToken: String = "",
    val isConnected: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
