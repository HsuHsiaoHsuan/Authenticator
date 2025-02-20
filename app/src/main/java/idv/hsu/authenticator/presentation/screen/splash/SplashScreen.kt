package idv.hsu.authenticator.presentation.screen.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2_000)
        onNavigateToNext()
    }
}