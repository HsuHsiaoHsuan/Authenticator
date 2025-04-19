package idv.hsu.authenticator.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import idv.hsu.authenticator.domain.GetFirstTimeOpenUseCase
import idv.hsu.authenticator.domain.InsertAccountUseCase
import idv.hsu.authenticator.domain.SetFirstTimeOpenUseCase
import idv.hsu.authenticator.presentation.utils.convertTotpDataToTOTPAccount
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val insertAccountUseCase: InsertAccountUseCase,
    private val getFirstTimeOpenUseCase: GetFirstTimeOpenUseCase,
    private val setFirstTimeOpenUseCase: SetFirstTimeOpenUseCase,
) : MVIViewModel<MainIntent, MainUiState>(
    initialUi = MainUiState.Idle
) {

    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SaveTotpAccount -> saveTotpAccount(intent.totpData)
        }
    }

    private fun saveTotpAccount(totpData: String) = viewModelScope.launch {
        setUiState(MainUiState.Loading)
        val data = convertTotpDataToTOTPAccount(totpData)
        if (data == null) {
            setUiState(MainUiState.SaveTotpAccountFailed("Invalid QR Code"))
        } else {
            val result = insertAccountUseCase(data)
            if (result < 1) {
                setUiState(MainUiState.SaveTotpAccountFailed("Duplicated."))
            } else {
                setUiState(MainUiState.SaveTotpAccountSuccess)
            }
        }
    }
}

sealed class MainIntent {
    data class SaveTotpAccount(val totpData: String) : MainIntent()
}

sealed class MainUiState {
    data object Idle : MainUiState()
    data object Loading : MainUiState()
    data object SaveTotpAccountSuccess : MainUiState()
    data class SaveTotpAccountFailed(val message: String) : MainUiState()
}