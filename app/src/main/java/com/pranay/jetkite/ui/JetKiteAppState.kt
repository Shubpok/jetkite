package com.pranay.jetkite.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.tracing.trace
import com.pranay.jetkite.navigation.JetKiteNavDestination

@Composable
fun rememberJetKiteAppState(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController()
): JetKiteAppState {
    return remember(navController) {
        JetKiteAppState(navController = navController, windowSizeClass = windowSizeClass)
    }
}

@Stable
class JetKiteAppState(
    val navController: NavHostController,
    val windowSizeClass: WindowSizeClass
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    /**
     * UI logic for navigating to a particular destination in the app. The NavigationOptions to
     * navigate with are based on the type of destination, which could be a top level destination or
     * just a regular destination.
     *
     * Top level destinations have only one copy of the destination of the back stack, and save and
     * restore state whenever you navigate to and from it.
     * Regular destinations can have multiple copies in the back stack and state isn't saved nor
     * restored.
     *
     * @param destination: The [JetKiteNavDestination] the app needs to navigate to.
     * @param route: Optional route to navigate to in case the destination contains arguments.
     */
    fun navigate(
        destination: JetKiteNavDestination,
        route: String? = null,
        clearStack: Boolean = true
    ) {
        trace("Navigation: $destination") {
            // if (destination is TopLevelDestination) {
            navController.navigate(route ?: destination.route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // re-selecting the same item
                launchSingleTop = true
                // Restore state when re-selecting a previously selected item
                restoreState = true
                if (clearStack) {
                    popUpTo(0)
                }
            }
            /*} else {
                navController.navigate(route ?: destination.route)
            }*/
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}
