package idv.hsu.authenticator.presentation.viewmodel

import idv.hsu.authenticator.domain.InsertAccountUseCase
import idv.hsu.authenticator.presentation.utils.convertTotpDataToTOTPAccount
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class QrCodeReaderViewModel(
    private val insertAccountUseCase: InsertAccountUseCase
) : MVIViewModel<QrCodeReaderIntent, QrCodeReaderUiState>(
    initialUi = QrCodeReaderUiState.Idle
) {
    override suspend fun handleIntent(intent: QrCodeReaderIntent) {
        when (intent) {
            is QrCodeReaderIntent.SaveTOTPAccount -> {
                val qrCodeData = intent.totpData
                setUiState(QrCodeReaderUiState.Loading)
                val account = convertTotpDataToTOTPAccount(qrCodeData)
                if (account == null) {
                    setUiState(QrCodeReaderUiState.SaveTOTPDataFailed("Invalid QR Code"))
                    return
                }

                val result = insertAccountUseCase(account)
                if (result > 0) {
                    setUiState(QrCodeReaderUiState.SaveTOTPDataSuccess)
                } else {
                    setUiState(QrCodeReaderUiState.SaveTOTPDataFailed("Duplicated."))
                }
            }
        }
    }
}


sealed class QrCodeReaderIntent {
    data class SaveTOTPAccount(val totpData: String) : QrCodeReaderIntent()
}

sealed class QrCodeReaderUiState {
    data object Idle : QrCodeReaderUiState()
    data object Loading : QrCodeReaderUiState()
    data object SaveTOTPDataSuccess : QrCodeReaderUiState()
    data class SaveTOTPDataFailed(val errorMessage: String) : QrCodeReaderUiState()
}
