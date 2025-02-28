package idv.hsu.authenticator.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.data.entities.TotpDataItem
import idv.hsu.authenticator.domain.DeleteAccountUseCase
import idv.hsu.authenticator.domain.GetAllAccountsUseCase
import idv.hsu.authenticator.domain.InsertAccountUseCase
import idv.hsu.authenticator.presentation.utils.convertTotpDataToTOTPAccount
import idv.hsu.authenticator.presentation.utils.generateTOTP
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotpViewModel @Inject constructor(
    private val insertAccountUseCase: InsertAccountUseCase,
    private val getAllAccountsUseCase: GetAllAccountsUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : MVIViewModel<TotpIntent, TotpUiState>(
    initialUi = TotpUiState.Idle
) {
    private val _remainingTime = MutableStateFlow(30L)
    private val remainingTime: StateFlow<Long> = _remainingTime

    init {
        startCountdownTimer()
        observeAccounts()
    }

    override suspend fun handleIntent(intent: TotpIntent) {
        when (intent) {
            is TotpIntent.SaveTOTPAccount -> {
                setUiState(TotpUiState.Loading)
                val data = convertTotpDataToTOTPAccount(intent.totpData)
                if (data == null) {
                    setUiState(TotpUiState.SaveTOTPAccountFailed("Invalid QR Code"))
                } else {
                    insertAccountUseCase(data)
                    setUiState(TotpUiState.SaveTOTPAccountSuccess)
                }
            }

            is TotpIntent.DeleteTOTPAccount -> {
                deleteAccountUseCase(intent.accountName)
                setUiState(TotpUiState.DeleteTOTPAccountSuccess(intent.accountName))
            }

        }
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                val timeRemaining = 30L - ((System.currentTimeMillis() / 1000) % 30L)
                _remainingTime.value = timeRemaining
                delay(1_000L)
            }
        }
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            combine(
                getAllAccountsUseCase(),
                remainingTime
            ) { accountList, remainingTime ->
                val groupedData =
                    accountList.sortedWith(compareBy({ it.issuer }, { it.accountName }))
                        .map { account ->
                            TotpDataItem(
                                accountName = account.accountName,
                                secret = generateTOTP(
                                    account.secret,
                                    System.currentTimeMillis() / 1000
                                ),
                                issuer = account.issuer,
                                remainingTime = remainingTime
                            )
                        }.groupBy { it.issuer ?: "Others" }
                accountList to groupedData
            }.collect { (accountList, groupedData) ->
                if (accountList.isNotEmpty()) {
                    setUiState(TotpUiState.ShowTOTPAccounts(groupedData))
                } else {
                    setUiState(TotpUiState.NoTotpAccount)
                }
            }
        }
    }
}

sealed class TotpIntent {
    data class SaveTOTPAccount(val totpData: String) : TotpIntent()
    data class DeleteTOTPAccount(val accountName: String) : TotpIntent()
}

sealed class TotpUiState {
    data object Idle : TotpUiState()
    data object Loading : TotpUiState()
    data class ShowTOTPAccounts(val accountsGroup: Map<String, List<TotpDataItem>>) : TotpUiState()
    data object NoTotpAccount : TotpUiState()
    data object SaveTOTPAccountSuccess : TotpUiState()
    data class SaveTOTPAccountFailed(val message: String) : TotpUiState()
    data class DeleteTOTPAccountSuccess(val accountName: String) : TotpUiState()
    data class DeleteTOTPAccountFailed(val accountName: String) : TotpUiState()
}