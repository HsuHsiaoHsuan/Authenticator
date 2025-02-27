package idv.hsu.authenticator.presentation.screen.tutorial


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Back
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import idv.hsu.authenticator.R
import idv.hsu.authenticator.ui.theme.colorNV700
import idv.hsu.authenticator.ui.theme.colorNV900
import idv.hsu.authenticator.ui.theme.colorP300
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onNavigateToNext: () -> Unit
) {
    val pages = listOf(
        TutorialPageScreen.First,
        TutorialPageScreen.Second,
        TutorialPageScreen.Third,
        TutorialPageScreen.Fourth
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

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
                    .fillMaxWidth()
            ) { page ->
                ConstraintLayout(
                    modifier = Modifier.fillMaxSize()
                ) {
                    pages[page].content()
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                            .background(Color.White),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (pagerState.currentPage > 0) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        if (pagerState.currentPage > 0) {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }
                                },
                                border = BorderStroke(
                                    1.4.dp,
                                    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else colorNV700
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isSystemInDarkTheme()) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        colorNV900
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                            ) {
                                Text(
                                    text = "Back",
                                    fontWeight = FontWeight(590),
                                    fontSize = 16.sp,
                                    color = if (isSystemInDarkTheme()) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else colorNV900
                                )

                            }
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    if (pagerState.currentPage < pages.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            },
                            enabled = pagerState.currentPage < pages.size - 1,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorP300,
                                contentColor = colorNV900,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.LightGray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                        ) {
                            Text(
                                text = "Next",
                                fontWeight = FontWeight(590),
                                fontSize = 16.sp,
                                color = if (isSystemInDarkTheme()) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else colorNV900
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class TutorialPageScreen(val content: @Composable () -> Unit) {
    object First : TutorialPageScreen({ TutorialFirst() })
    object Second : TutorialPageScreen({ TutorialSecond() })
    object Third : TutorialPageScreen({ TutorialThird() })
    object Fourth : TutorialPageScreen({ TutorialFourth() })
}