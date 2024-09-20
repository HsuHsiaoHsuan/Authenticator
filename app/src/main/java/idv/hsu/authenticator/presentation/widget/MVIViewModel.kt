package idv.hsu.authenticator.presentation.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class MVIViewModel<UserIntent, UiState>(
    initialUi: UiState
) : ViewModel() {

    private val userIntent = MutableSharedFlow<UserIntent>()

    private val _uiStateFlow = MutableStateFlow(initialUi)
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow

    protected abstract suspend fun handleIntent(intent: UserIntent)

    init {
        viewModelScope.launch {
            userIntent.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    fun onIntent(event: UserIntent) {
        viewModelScope.launch {
            userIntent.emit(event)
        }
    }

    suspend fun setUiState(uiState: UiState) {
        _uiStateFlow.emit(uiState)
    }
}