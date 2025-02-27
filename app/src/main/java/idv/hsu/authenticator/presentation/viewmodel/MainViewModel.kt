package idv.hsu.authenticator.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.domain.DbInsertAccountUseCase
import idv.hsu.authenticator.domain.DsGetFirstTimeStatusUseCase
import idv.hsu.authenticator.domain.DsSetFirstTimeStatusUseCase
import idv.hsu.authenticator.presentation.utils.convertTotpDataToTOTPAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dbInsertAccountUseCase: DbInsertAccountUseCase,
    private val dsGetFirstTimeStatusUseCase: DsGetFirstTimeStatusUseCase,
    private val dsSetFirstTimeStatusUseCase: DsSetFirstTimeStatusUseCase,
) : MVIViewModel<MainIntent, MainUiState>(
    initialUi = MainUiState.Idle
) {
    private val _isFirstTime = MutableStateFlow<Boolean>(true)
    val isFirstTime: StateFlow<Boolean> = _isFirstTime.asStateFlow()

    init {
        viewModelScope.launch {
            _isFirstTime.value = dsGetFirstTimeStatusUseCase()
        }
    }

    suspend fun markFirstTimeDone() {
        dsSetFirstTimeStatusUseCase()
        _isFirstTime.value = false
    }

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
            val result = dbInsertAccountUseCase(data)
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