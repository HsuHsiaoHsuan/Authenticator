package idv.hsu.authenticator.presentation.screen.tutorial.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import idv.hsu.authenticator.R
import idv.hsu.authenticator.ui.theme.colorNV700
import idv.hsu.authenticator.ui.theme.colorNV900
import idv.hsu.authenticator.ui.theme.colorP300
import kotlinx.coroutines.launch

@Composable
fun TutorialNavigationButtons(
    pagerState: PagerState,
    doneButtonAction: () -> Unit
) {
    val scope = rememberCoroutineScope()

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
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.action_back),
                            fontWeight = FontWeight(590),
                            fontSize = 16.sp,
                            color = if (isSystemInDarkTheme()) {
                                MaterialTheme.colorScheme.onPrimary
                            } else colorNV900
                        )

                    }
                }

                val isFinalPage = pagerState.currentPage == pagerState.pageCount - 1
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorP300,
                        contentColor = colorNV900,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        if (isFinalPage) {
                            doneButtonAction.invoke()
                        } else {
                            scope.launch {
                                if (pagerState.currentPage < pagerState.pageCount - 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text(
                        text = stringResource(if (isFinalPage) R.string.action_done else R.string.action_next),
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