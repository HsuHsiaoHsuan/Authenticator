package idv.hsu.authenticator.presentation.screen.splash

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import idv.hsu.authenticator.SplashRoute
import idv.hsu.authenticator.TotpListRoute
import idv.hsu.authenticator.TutorialRoute
import idv.hsu.authenticator.presentation.viewmodel.FirstTimeOpenViewModel
import idv.hsu.authenticator.ui.theme.colorNV900
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: FirstTimeOpenViewModel = koinViewModel()
) {

    val isFirstTime = viewModel.isFirstTime.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        delay(1_000)
        if (isFirstTime.value) {
            navController.navigate(TutorialRoute.route) {
                popUpTo(SplashRoute.route) { inclusive = true }
            }
        } else {
            navController.navigate(TotpListRoute.route) {
                popUpTo(SplashRoute.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Splash Screen",
            fontSize = 32.sp,
            color = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.onBackground
            } else {
                colorNV900
            }
        )
    }
}