package idv.hsu.authenticator.presentation.screen.tutorial.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import idv.hsu.authenticator.R
import idv.hsu.authenticator.presentation.screen.tutorial.components.SubtitleText
import idv.hsu.authenticator.presentation.screen.tutorial.components.TitleText

@Composable
fun TutorialPage(
    modifier: Modifier = Modifier,
    titleRes: Int,
    subtitleRes: Int
) {
    Column(
        modifier = modifier
    ) {
        TitleText(
            textRes = titleRes
        )

        Spacer(modifier = Modifier.height(8.dp))

        SubtitleText(
            textRes = subtitleRes
        )
    }
}