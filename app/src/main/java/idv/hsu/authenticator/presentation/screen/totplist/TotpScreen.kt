package idv.hsu.authenticator.presentation.screen.totplist

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import idv.hsu.authenticator.R
import idv.hsu.authenticator.presentation.screen.totplist.pages.TotpEmptyPage
import idv.hsu.authenticator.presentation.screen.totplist.pages.TotpListPage
import idv.hsu.authenticator.presentation.viewmodel.TotpUiState
import idv.hsu.authenticator.presentation.viewmodel.TotpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotpScreen(
    onUpdateTopAppActions: (@Composable (() -> Unit)?) -> Unit,
    needShowFloatActionButton: (Boolean) -> Unit,
    onStartNowAction: () -> Unit,
    navController: NavController,
) {
    val viewModel: TotpViewModel = hiltViewModel<TotpViewModel>()
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle().value
    var appbarActions: @Composable (() -> Unit)? = null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.screen_totp)) },
                colors = TopAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                    scrolledContainerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                    navigationIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                    titleContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else Color.Black,
                    actionIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else Color.Black
                )
            )
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 5.dp
        ) {
            when (uiState) {
                is TotpUiState.Idle -> Unit
                is TotpUiState.Loading -> Unit
                is TotpUiState.SaveTOTPAccountFailed -> Unit
                is TotpUiState.ShowTOTPAccounts -> {
                    needShowFloatActionButton(true)
                    TotpListPage(
                        data = uiState.accountsGroup,
                        modifier = Modifier.fillMaxSize()
                    )
                    appbarActions = {
                        IconButton(onClick = {
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = {
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add"
                            )

                        }
                    }
                }

                is TotpUiState.NoTotpAccount -> {
                    needShowFloatActionButton(false)
                    TotpEmptyPage(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        onStartNowAction = onStartNowAction
                    )
                    appbarActions = null
                }

                is TotpUiState.SaveTOTPAccountSuccess,
                is TotpUiState.DeleteTOTPAccountFailed,
                is TotpUiState.DeleteTOTPAccountSuccess -> Unit
            }

            onUpdateTopAppActions(appbarActions)
        }
    }
}