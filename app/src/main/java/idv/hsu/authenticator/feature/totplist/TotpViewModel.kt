package idv.hsu.authenticator.feature.totplist

import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.MVIViewModel
import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.domain.DbDeleteAccountUseCase
import idv.hsu.authenticator.domain.DbGetAllAccountsUseCase
import idv.hsu.authenticator.domain.DbInsertAccountUseCase
import idv.hsu.authenticator.feature.qrcodereader.QrCodeReaderUiState
import idv.hsu.authenticator.utils.convertTotpDataToTOTPAccount
import javax.inject.Inject

@HiltViewModel
class TotpViewModel @Inject constructor(
    private val dbInsertAccountUseCase: DbInsertAccountUseCase,
    private val dbGetAllAccountsUseCase: DbGetAllAccountsUseCase,
    private val dbDeleteAccountUseCase: DbDeleteAccountUseCase,
) : MVIViewModel<TotpIntent, TotpUiState>(
    initialUi = TotpUiState.Idle
) {
    override suspend fun handleIntent(intent: TotpIntent) {
        when (intent) {
            is TotpIntent.LoadTOTPAccounts -> {
                setUiState(TotpUiState.ShowTOTPAccounts(dbGetAllAccountsUseCase()))
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
}

sealed class TotpIntent {
    data object LoadTOTPAccounts : TotpIntent()
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