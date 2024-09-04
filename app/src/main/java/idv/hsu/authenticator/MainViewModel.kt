package idv.hsu.authenticator

import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.domain.DbGetAllAccountsUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dbGetAllAccountsUseCase: DbGetAllAccountsUseCase
) : MVIViewModel<MainViewIntent, MainViewUiState>(
    initialUi = MainViewUiState.Idle
) {
    override suspend fun handleIntent(intent: MainViewIntent) {
        when (intent) {
            is MainViewIntent.CheckTOTPAvailable -> {
                dbGetAllAccountsUseCase().let { accounts ->
                    if (accounts.isEmpty()) {
                        setUiState(MainViewUiState.TOTPEmpty)
                    } else {
                        setUiState(MainViewUiState.TOTPAvailable)
                    }
                }
            }
        }
    }
}

sealed class MainViewIntent {
    data object CheckTOTPAvailable : MainViewIntent()
}

sealed class MainViewUiState {
    data object Idle : MainViewUiState()
    data object TOTPAvailable : MainViewUiState()
    data object TOTPEmpty : MainViewUiState()
    data class CheckTOTPError(val errorMessage: String) : MainViewUiState()
}