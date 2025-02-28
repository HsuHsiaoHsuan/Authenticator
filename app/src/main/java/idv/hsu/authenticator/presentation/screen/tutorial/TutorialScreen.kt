package idv.hsu.authenticator.presentation.screen.tutorial


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import idv.hsu.authenticator.R
import idv.hsu.authenticator.Screen
import idv.hsu.authenticator.presentation.screen.tutorial.components.TutorialNavigationButtons
import idv.hsu.authenticator.presentation.screen.tutorial.pages.TutorialPage
import idv.hsu.authenticator.presentation.viewmodel.FirstTimeOpenViewModel
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: FirstTimeOpenViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val pages = createTutorialPages()
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 5.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                userScrollEnabled = false
            ) { page ->
                pages[page].content()
            }

            TutorialNavigationButtons(
                pagerState = pagerState,
                doneButtonAction = {
                    scope.launch {
                        viewModel.markFirstTimeDone()
                    }
                    navController.navigate(Screen.Totp.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun createTutorialPages(): List<TutorialPageData> {
    val pageModifier = Modifier
        .padding(top = 12.dp, start = 30.dp, end = 30.dp)
        .fillMaxSize()

    return listOf<TutorialPageData>(
        TutorialPageData(
            modifier = pageModifier,
            titleRes = R.string.tutorial_page1_title,
            subtitleRes = R.string.tutorial_page1_subtitle
        ),
        TutorialPageData(
            modifier = pageModifier,
            titleRes = R.string.tutorial_page2_title,
            subtitleRes = R.string.tutorial_page2_subtitle
        ),
        TutorialPageData(
            modifier = pageModifier,
            titleRes = R.string.tutorial_page3_title,
            subtitleRes = R.string.tutorial_page3_subtitle
        ),
        TutorialPageData(
            modifier = pageModifier,
            titleRes = R.string.tutorial_page4_title,
            subtitleRes = R.string.tutorial_page4_subtitle
        )
    )
}

data class TutorialPageData(
    val modifier: Modifier,
    val titleRes: Int,
    val subtitleRes: Int,
    val content: @Composable () -> Unit = {
        TutorialPage(
            modifier = modifier,
            titleRes = titleRes,
            subtitleRes = subtitleRes
        )
    }
)