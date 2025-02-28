package idv.hsu.authenticator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.authenticator.domain.GetFirstTimeOpenUseCase
import idv.hsu.authenticator.domain.SetFirstTimeOpenUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FirstTimeOpenViewModel @Inject constructor(
    private val getFirstTimeOpenUseCase: GetFirstTimeOpenUseCase,
    private val setFirstTimeOpenUseCase: SetFirstTimeOpenUseCase,
) : ViewModel() {

    val isFirstTime = getFirstTimeOpenUseCase()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            true
        )

    suspend fun markFirstTimeDone() {
        setFirstTimeOpenUseCase(false)
    }
}
