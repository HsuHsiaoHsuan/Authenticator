package idv.hsu.authenticator.presentation.screen.splash

import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.presentation.widget.MVIViewModel
import idv.hsu.authenticator.domain.DbGetAllAccountsUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dbGetAllAccountsUseCase: DbGetAllAccountsUseCase
) : MVIViewModel<SplashIntent, SplashUiState>(
    initialUi = SplashUiState.Idle
) {
    override suspend fun handleIntent(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.CheckTOTPAccount -> {
                val accounts = dbGetAllAccountsUseCase()
                if (accounts.isEmpty()) {
                    setUiState(SplashUiState.CheckTOTPAccountEmptyOrFailed)
                } else {
                    setUiState(SplashUiState.CheckTOTPAccountSuccess)
                }
            }
        }
    }
}

sealed class SplashIntent {
    data object CheckTOTPAccount : SplashIntent()
}

sealed class SplashUiState {
    data object Idle : SplashUiState()
    data object CheckTOTPAccountSuccess : SplashUiState()
    data object CheckTOTPAccountEmptyOrFailed : SplashUiState()
}