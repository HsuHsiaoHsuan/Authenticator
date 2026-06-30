package idv.hsu.authenticator.presentation.screen.totplist

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import idv.hsu.authenticator.R
import idv.hsu.authenticator.presentation.screen.totplist.pages.TotpEmptyPage
import idv.hsu.authenticator.presentation.screen.totplist.pages.TotpListPage
import idv.hsu.authenticator.presentation.viewmodel.TotpUiState
import idv.hsu.authenticator.presentation.viewmodel.TotpViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotpListScreen(
    scanQrCodeAction: () -> Unit,
    navController: NavController,
) {
    val viewModel: TotpViewModel = koinViewModel()
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle().value

    var isSearching by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Crossfade(
                modifier = Modifier.animateContentSize(),
                targetState = isSearching,
                label = "Search"
            ) { target ->
                if (target) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(TopAppBarDefaults.windowInsets),
                        value = value,
                        placeholder = { Text(text = "Search ...") },
                        onValueChange = { value = it },
                        leadingIcon = {
                            IconButton(
                                onClick = {
                                    isSearching = false
                                    value = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    value = ""
                                },
                                enabled = value.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    )
                } else {
                    TopAppBar(
                        title = { Text(text = stringResource(R.string.screen_totp)) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                            scrolledContainerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                            navigationIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                            titleContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else Color.Black,
                            actionIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else Color.Black
                        ),
                        actions = {
                            if (uiState is TotpUiState.ShowTOTPAccounts) {
                                IconButton(onClick = { isSearching = true }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = "Search"
                                    )
                                }
                                IconButton(onClick = scanQrCodeAction) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        }
                    )
                }
            }
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
                    val query = value.trim()
                    val accountsGroup = if (query.isBlank()) {
                        uiState.accountsGroup
                    } else {
                        uiState.accountsGroup
                            .mapValues { (_, accounts) ->
                                accounts.filter { account ->
                                    account.accountName.contains(query, ignoreCase = true) ||
                                            account.issuer.orEmpty().contains(query, ignoreCase = true)
                                }
                            }
                            .filterValues { it.isNotEmpty() }
                    }

                    TotpListPage(
                        data = accountsGroup,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is TotpUiState.NoTotpAccount -> {
                    TotpEmptyPage(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        onStartNowAction = scanQrCodeAction
                    )
                }

                is TotpUiState.SaveTOTPAccountSuccess,
                is TotpUiState.DeleteTOTPAccountFailed,
                is TotpUiState.DeleteTOTPAccountSuccess -> Unit
            }
        }
    }
}
