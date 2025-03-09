package idv.hsu.authenticator

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

sealed interface ScreenRouteDestination {
    val route: String
}

data object SplashRoute: ScreenRouteDestination {
    override val route = "splash"
}

data object TutorialRoute: ScreenRouteDestination {
    override val route = "tutorial"
}

data object TotpListRoute: ScreenRouteDestination {
    override val route = "totp_list"
    const val isFirstTimeOpenAppArg = "is_first_time"
    val routeWithArg = "$route/{$isFirstTimeOpenAppArg}"
    val arguments = listOf(
        navArgument(isFirstTimeOpenAppArg) { type = NavType.BoolType }
    )
    val deepLinks = listOf(
        navDeepLink { uriPattern = "freeman://$route/{$isFirstTimeOpenAppArg}" }
    )
}