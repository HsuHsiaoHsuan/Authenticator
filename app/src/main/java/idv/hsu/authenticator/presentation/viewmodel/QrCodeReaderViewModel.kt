package idv.hsu.authenticator.presentation.viewmodel

import android.net.Uri
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.domain.DeleteAccountUseCase
import idv.hsu.authenticator.domain.GetAccountUseCase
import idv.hsu.authenticator.domain.GetAllAccountsUseCase
import idv.hsu.authenticator.domain.InsertAccountUseCase
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class QrCodeReaderViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val getAllAccountsUseCase: GetAllAccountsUseCase,
    private val insertAccountUseCase: InsertAccountUseCase
) : MVIViewModel<QrCodeReaderIntent, QrCodeReaderUiState>(
    initialUi = QrCodeReaderUiState.Idle
) {
    override suspend fun handleIntent(intent: QrCodeReaderIntent) {
        when (intent) {
            is QrCodeReaderIntent.SaveTOTPAccount -> {
                val qrCodeData = intent.totpData
                setUiState(QrCodeReaderUiState.Loading)
                if (qrCodeData.startsWith("otpauth://totp/")) {
                    val uri = Uri.parse(qrCodeData)
                    val accountName = uri.path?.substring(1) // 去掉前面的 "/"
                    val secret = uri.getQueryParameter("secret")
                    val issuer = uri.getQueryParameter("issuer") ?: ""
                    Timber.d("insert accountName: $accountName")
                    Timber.d("insert secret: $secret")
                    Timber.d("insert issuer: $issuer")

                    if (secret != null && accountName != null) {
                        val result = insertAccountUseCase(
                            TOTPAccount(
                                accountName = accountName,
                                secret = secret,
                                issuer = issuer
                            )
                        )
                        Timber.d("Insert result: $result")
                    } else {
                        setUiState(QrCodeReaderUiState.SaveTOPTDataFailed("Invalid QR Code"))
                    }
                } else {
                    setUiState(QrCodeReaderUiState.SaveTOPTDataFailed("Unsupported QR Code format"))
                }
                setUiState(QrCodeReaderUiState.SaveTOPTDataSuccess)
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
    data object SaveTOPTDataSuccess : QrCodeReaderUiState()
    data class SaveTOPTDataFailed(val errorMessage: String) : QrCodeReaderUiState()
}