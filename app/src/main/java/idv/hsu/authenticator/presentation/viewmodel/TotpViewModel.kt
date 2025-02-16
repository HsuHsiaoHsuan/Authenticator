package idv.hsu.authenticator.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.domain.DbDeleteAccountUseCase
import idv.hsu.authenticator.domain.DbGetAllAccountsUseCase
import idv.hsu.authenticator.domain.DbInsertAccountUseCase
import idv.hsu.authenticator.presentation.utils.convertTotpDataToTOTPAccount
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class TotpViewModel @Inject constructor(
    private val dbInsertAccountUseCase: DbInsertAccountUseCase,
    private val dbGetAllAccountsUseCase: DbGetAllAccountsUseCase,
    private val dbDeleteAccountUseCase: DbDeleteAccountUseCase,
) : MVIViewModel<TotpIntent, TotpUiState>(
    initialUi = TotpUiState.Idle
) {
    private val _remainingTime = MutableStateFlow(30L)
    val remainingTime: StateFlow<Long> = _remainingTime

    init {
        startCountdownTimer()
        observeAccounts()
    }

    override suspend fun handleIntent(intent: TotpIntent) {
        when (intent) {
            is TotpIntent.RefreshTOTPAccounts -> {
                refreshAccounts()
            }
            is TotpIntent.SaveTOTPAccount -> {
                setUiState(TotpUiState.Loading)
                val data = convertTotpDataToTOTPAccount(intent.totpData)
                if (data == null) {
                    setUiState(TotpUiState.SaveTOTPAccountFailed("Invalid QR Code"))
                } else {
                    dbInsertAccountUseCase(data)
                    setUiState(TotpUiState.SaveTOTPAccountSuccess)
                }
            }
            is TotpIntent.DeleteTOTPAccount -> {
                dbDeleteAccountUseCase(intent.accountName)
                setUiState(TotpUiState.DeleteTOTPAccountSuccess(intent.accountName))
            }

        }
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                val timeRemaining = 30L - ((System.currentTimeMillis() / 1000) % 30L)
                _remainingTime.value = timeRemaining
                delay(1000L)
            }
        }
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            dbGetAllAccountsUseCase().collect { accountList ->
                setUiState(TotpUiState.ShowTOTPAccounts(accountList))
            }
        }
    }

    private fun refreshAccounts() {
        viewModelScope.launch {
            val latestAccounts = dbGetAllAccountsUseCase().first()
            setUiState(TotpUiState.ShowTOTPAccounts(latestAccounts))
        }
    }
}

sealed class TotpIntent {
    data object RefreshTOTPAccounts : TotpIntent()
    data class SaveTOTPAccount(val totpData: String) : TotpIntent()
    data class DeleteTOTPAccount(val accountName: String) : TotpIntent()
}

sealed class TotpUiState {
    data object Idle : TotpUiState()
    data object Loading : TotpUiState()
    data class ShowTOTPAccounts(val accounts: List<TOTPAccount>) : TotpUiState()
    data object SaveTOTPAccountSuccess: TotpUiState()
    data class SaveTOTPAccountFailed(val message: String) : TotpUiState()
    data class DeleteTOTPAccountSuccess(val accountName: String) : TotpUiState()
    data class DeleteTOTPAccountFailed(val accountName: String) : TotpUiState()
}