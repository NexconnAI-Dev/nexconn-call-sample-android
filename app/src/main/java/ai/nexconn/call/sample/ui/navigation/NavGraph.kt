package ai.nexconn.call.sample.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ai.nexconn.call.sample.ui.login.LoginScreen
import ai.nexconn.call.sample.ui.call.single.SingleCallScreen
import ai.nexconn.call.sample.ui.call.multi.MultiCallScreen

/**
 * Navigation routes for the app
 */
object Routes {
    const val LOGIN = "login"
    const val SINGLE_CALL = "single_call"
    const val MULTI_CALL = "multi_call"
}

/**
 * Main navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToSingleCall = { navController.navigate(Routes.SINGLE_CALL) },
                onNavigateToMultiCall = { navController.navigate(Routes.MULTI_CALL) }
            )
        }

        composable(Routes.SINGLE_CALL) {
            SingleCallScreen()
        }

        composable(Routes.MULTI_CALL) {
            MultiCallScreen()
        }
    }
}
