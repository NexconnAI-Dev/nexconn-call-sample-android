package ai.nexconn.call.sample.ui.call

import android.content.Context
import androidx.lifecycle.ViewModel
import ai.nexconn.call.api.NCCallEngine
import ai.nexconn.call.api.handler.NCCallAPIResultHandler
import ai.nexconn.call.api.handler.NCCallEventHandler

/**
 * Base ViewModel for call screens
 * Manages handler registration and unregistration
 */
abstract class CallViewModel(protected val context: Context) : ViewModel() {

    protected val callEngine: NCCallEngine = NCCallEngine.getInstance()

    /**
     * Subclasses must implement to create their specific event handler
     */
    protected abstract fun createEventHandler(): NCCallEventHandler

    /**
     * Subclasses must implement to create their specific result handler
     */
    protected abstract fun createResultHandler(): NCCallAPIResultHandler

    /**
     * Register handlers when entering the screen
     */
    fun registerHandlers() {
        callEngine.setCallEventHandler(createEventHandler())
        callEngine.setAPIResultHandler(createResultHandler())
    }

    /**
     * Unregister handlers when exiting the screen
     */
    fun unregisterHandlers() {
        callEngine.setCallEventHandler(null)
        callEngine.setAPIResultHandler(null)
    }

    override fun onCleared() {
        super.onCleared()
        unregisterHandlers()
    }
}
