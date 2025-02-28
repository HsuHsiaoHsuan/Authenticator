package idv.hsu.authenticator.presentation.screen.tutorial.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(modifier: Modifier = Modifier, textRes: Int) {
    Box(modifier = modifier) {
        Text(
            text = stringResource(textRes),
            fontWeight = FontWeight(700),
            lineHeight = 34.sp,
            fontSize = 28.sp
        )
    }
}