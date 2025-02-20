package idv.hsu.authenticator.presentation.screen.tutorial


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import idv.hsu.authenticator.R

@Composable
fun TutorialScreen(onFinishTutorial: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.tutorial_welcome_to_authenticator))
        Button(onClick = onFinishTutorial) {
            Text(stringResource(R.string.tutorial_get_start))
        }
    }
}