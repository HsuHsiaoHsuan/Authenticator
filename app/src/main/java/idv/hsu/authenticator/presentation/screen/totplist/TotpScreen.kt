package idv.hsu.authenticator.presentation.screen.totplist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import idv.hsu.authenticator.R
import idv.hsu.authenticator.data.entities.TotpDataItem
import idv.hsu.authenticator.presentation.utils.generateTOTP
import idv.hsu.authenticator.presentation.viewmodel.TotpUiState
import idv.hsu.authenticator.presentation.viewmodel.TotpViewModel
import idv.hsu.authenticator.ui.theme.colorNV500
import idv.hsu.authenticator.ui.theme.colorNV900

@Composable
fun TotpScreen(
    modifier: Modifier = Modifier,
    onUpdateTopAppBar: (String, @Composable (() -> Unit)?) -> Unit,
    onEmptyStateChanged: (Boolean) -> Unit
) {
    val viewModel: TotpViewModel = viewModel<TotpViewModel>()
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle().value
    val remainingTime by viewModel.remainingTime.collectAsStateWithLifecycle()

//    LaunchedEffect(Unit) {
//        viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
//    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (uiState) {
            is TotpUiState.Idle -> Unit
            is TotpUiState.Loading -> Unit
            is TotpUiState.SaveTOTPAccountFailed -> Unit
            is TotpUiState.ShowTOTPAccounts -> {
                if (uiState.accounts.isNotEmpty()) {
                    onEmptyStateChanged(false)
                    TotpList(
                        items = uiState.accounts.map { account ->
                            TotpDataItem(
                                account.id,
                                account.accountName,
                                generateTOTP(account.secret, System.currentTimeMillis() / 1000),
                                account.issuer,
                                remainingTime
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    onEmptyStateChanged(true)
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateContentSize()
                    ) {
                        val (titleText, subtitleText, image, startButton, howItWorkButton) = createRefs()

                        Text(
                            text = stringResource(R.string.totp_no_data_title),
                            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else colorNV900,
                            fontWeight = FontWeight(700),
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            modifier = Modifier
                                .padding(top = 40.dp, start = 30.dp, end = 30.dp)
                                .constrainAs(titleText) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)

                                }
                        )

                        Text(
                            text = stringResource(R.string.totp_no_data_subtitle),
                            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else colorNV500,
                            fontWeight = FontWeight(590),
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier
                                .padding(top = 8.dp, start = 30.dp, end = 30.dp)
                                .constrainAs(subtitleText) {
                                    top.linkTo(titleText.bottom)
                                    start.linkTo(titleText.start)
                                    end.linkTo(titleText.end)
                                }
                        )
                    }
                }
            }

            is TotpUiState.SaveTOTPAccountSuccess,
            is TotpUiState.DeleteTOTPAccountFailed,
            is TotpUiState.DeleteTOTPAccountSuccess -> Unit
        }
    }
}

@Preview
@Composable
fun PasscodeBlockPreview() {
    PasscodeBlock(
        item = TotpDataItem(
            1,
            "Account Name",
            "Secret",
            "Issuer",
            30L
        ),
        isShowingPasscode = true,
        onPasscodeClick = {}
    )
}

@Preview
@Composable
fun TotpListItemPreview() {
    TotpListItem(
        item = TotpDataItem(
            1,
            "Account Name",
            "Secret",
            "Issuer",
            30L
        ),
        isShowingPasscode = true,
        onPasscodeClick = {}
    )
}