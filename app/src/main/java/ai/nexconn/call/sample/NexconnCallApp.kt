package ai.nexconn.call.sample

import ai.nexconn.call.api.NCCallEngine
import android.app.Application

/**
 * Application class for Nexconn Call Sample
 * Handles SDK initialization at app startup
 */
class NexconnCallApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Must be called before Chat SDK initialization
        NCCallEngine.install()
    }
}
