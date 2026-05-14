package ai.nexconn.call.sample.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Utility object for displaying Toast messages
 */
object ToastUtils {

    /**
     * Show a short Toast message with a string resource
     */
    fun showToast(context: Context, @StringRes messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }

    /**
     * Show a short Toast message with a string resource and format arguments
     */
    fun showToast(context: Context, @StringRes messageResId: Int, vararg formatArgs: Any) {
        val message = context.getString(messageResId, *formatArgs)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Show a short Toast message with a plain string
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
