package ai.nexconn.call.sample.ui.login

import ai.nexconn.call.api.NCCallEngine
import ai.nexconn.call.api.params.NCCallInitParams
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.nexconn.call.sample.R
import ai.nexconn.call.sample.utils.ToastUtils
import ai.nexconn.chat.NCEngine
import ai.nexconn.chat.error.NCError
import ai.nexconn.chat.handler.ConnectHandler
import ai.nexconn.chat.params.AreaCode.*
import ai.nexconn.chat.params.ConnectParams
import ai.nexconn.chat.params.InitParams
import ai.nexconn.chat.params.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login screen
 * Manages SDK initialization, connection, and disconnection
 */
class LoginViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Update app key input
     */
    fun onAppKeyChanged(appKey: String) {
        _uiState.update { it.copy(appKey = appKey) }
    }

    /**
     * Update user token input
     */
    fun onUserTokenChanged(userToken: String) {
        _uiState.update { it.copy(userToken = userToken) }
    }

    /**
     * Connect to Nexconn services
     * Follows the initialization order:
     * 1. Initialize Chat SDK
     * 2. Connect Chat SDK
     * 3. Initialize Call SDK
     */
    fun connect() {
        val appKey = _uiState.value.appKey.trim()
        val userToken = _uiState.value.userToken.trim()

        // Validate inputs
        if (appKey.isEmpty()) {
            ToastUtils.showToast(context, R.string.error_empty_app_key)
            return
        }

        if (userToken.isEmpty()) {
            ToastUtils.showToast(context, R.string.error_empty_user_token)
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // 1. Initialize Chat SDK
                val initParams = InitParams(context, appKey)
                initParams.logLevel = LogLevel.DEBUG
                initParams.areaCode = SG
                initParams.enablePush = false
                NCEngine.initialize(initParams)

                // 2. Connect Chat SDK
                val connectParams = ConnectParams(userToken)
                NCEngine.connect(connectParams, object : ConnectHandler {
                    override fun onResult(userId: String?, error: NCError?) {
                        if (error == null && userId != null) {
                            // 3. Initialize Call SDK after successful connection
                            val callInitParams = NCCallInitParams(context)
                            callInitParams.pubLowResolutionStream(true)
                            NCCallEngine.initialize(callInitParams)

                            _uiState.update {
                                it.copy(
                                    isConnected = true,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }
                            ToastUtils.showToast(context, R.string.toast_connection_success)
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = error?.message ?: "Unknown error"
                                )
                            }
                            ToastUtils.showToast(
                                context,
                                R.string.toast_connection_failed,
                                error?.message ?: "Unknown error"
                            )
                        }
                    }
                })
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
                ToastUtils.showToast(
                    context,
                    R.string.toast_connection_failed,
                    e.message ?: "Unknown error"
                )
            }
        }
    }

    /**
     * Disconnect from Nexconn services
     */
    fun disconnect() {
        viewModelScope.launch {
            try {
                NCEngine.disconnect()
                _uiState.update {
                    it.copy(
                        isConnected = false,
                        errorMessage = null
                    )
                }
                ToastUtils.showToast(context, R.string.toast_disconnected)
            } catch (e: Exception) {
                ToastUtils.showToast(context, e.message ?: "Disconnect failed")
            }
        }
    }
}
