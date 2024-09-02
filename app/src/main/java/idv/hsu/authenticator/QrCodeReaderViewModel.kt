package idv.hsu.authenticator

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.domain.DbDeleteAccountUseCase
import idv.hsu.authenticator.domain.DbGetAccountUseCase
import idv.hsu.authenticator.domain.DbGetAllAccountsUseCase
import idv.hsu.authenticator.domain.DbInsertAccountUseCase
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class QrCodeReaderViewModel @Inject constructor(
    private val dbDeleteAccountUseCase: DbDeleteAccountUseCase,
    private val dbGetAccountUseCase: DbGetAccountUseCase,
    private val dbGetAllAccountsUseCase: DbGetAllAccountsUseCase,
    private val dbInsertAccountUseCase: DbInsertAccountUseCase
) : MVIViewModel<QrCodeReaderIntent, QrCodeReaderUiState>(
    initialUi = QrCodeReaderUiState.Idle
) {
    override suspend fun handleIntent(intent: QrCodeReaderIntent) {
        when (intent) {
            is QrCodeReaderIntent.SaveTOPTAccount -> {
                val qrCodeData = intent.totpData
                setUiState(QrCodeReaderUiState.Loading)
                if (qrCodeData.startsWith("otpauth://totp/")) {
                    val uri = Uri.parse(qrCodeData)
                    val accountName = uri.path?.substring(1) // 去掉前面的 "/"
                    val secret = uri.getQueryParameter("secret")
                    val issuer = uri.getQueryParameter("issuer")
                    Timber.d("accountName: $accountName")
                    Timber.d("secret: $secret")
                    Timber.d("issuer: $issuer")

                    if (secret != null && accountName != null) {
                        dbInsertAccountUseCase(
                            TOTPAccount(
                                accountName = accountName,
                                secret = secret,
                                issuer = issuer
                            )
                        )
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
    data class SaveTOPTAccount(val totpData: String) : QrCodeReaderIntent()
}

sealed class QrCodeReaderUiState {
    data object Idle : QrCodeReaderUiState()
    data object Loading : QrCodeReaderUiState()
    data object SaveTOPTDataSuccess : QrCodeReaderUiState()
    data class SaveTOPTDataFailed(val errorMessage: String) : QrCodeReaderUiState()
}