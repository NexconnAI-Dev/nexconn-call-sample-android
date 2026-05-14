package ai.nexconn.call.sample.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * Utility class for handling runtime permissions
 */
object PermissionUtils {

    /**
     * Check if camera permission is granted
     */
    fun isCameraPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if microphone permission is granted
     */
    fun isMicrophonePermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if both camera and microphone permissions are granted
     */
    fun areCallPermissionsGranted(context: Context): Boolean {
        return isCameraPermissionGranted(context) && isMicrophonePermissionGranted(context)
    }

    /**
     * Create permission launcher for camera and microphone
     */
    fun createPermissionLauncher(
        activity: ComponentActivity,
        onPermissionsGranted: () -> Unit,
        onPermissionsDenied: () -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
        }
    }

    /**
     * Request camera and microphone permissions
     */
    fun requestCallPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }
}
