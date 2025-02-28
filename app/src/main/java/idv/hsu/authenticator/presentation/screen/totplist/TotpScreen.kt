package idv.hsu.authenticator.presentation.screen.totplist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import idv.hsu.authenticator.presentation.screen.totplist.pages.TotpEmptyPage
import idv.hsu.authenticator.presentation.screen.totplist.pages.TotpListPage
import idv.hsu.authenticator.presentation.viewmodel.TotpUiState
import idv.hsu.authenticator.presentation.viewmodel.TotpViewModel

@Composable
fun TotpScreen(
    modifier: Modifier = Modifier,
    onUpdateTopAppActions: (@Composable (() -> Unit)?) -> Unit,
    needShowFloatActionButton: (Boolean) -> Unit,
    onStartNowAction: () -> Unit,
    navController: NavController,
) {
    val viewModel: TotpViewModel = hiltViewModel<TotpViewModel>()
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle().value

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 5.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState) {
                is TotpUiState.Idle -> Unit
                is TotpUiState.Loading -> Unit
                is TotpUiState.SaveTOTPAccountFailed -> Unit
                is TotpUiState.ShowTOTPAccounts -> {
                    needShowFloatActionButton(true)
                    TotpListPage(
                        data = uiState.accountsGroup,
                        modifier = Modifier
                    )
                }

                is TotpUiState.NoTotpAccount -> {
                    needShowFloatActionButton(false)
                    TotpEmptyPage(
                        navController = navController,
                        onStartNowAction = onStartNowAction)
                }

                is TotpUiState.SaveTOTPAccountSuccess,
                is TotpUiState.DeleteTOTPAccountFailed,
                is TotpUiState.DeleteTOTPAccountSuccess -> Unit
            }
        }
    }
}