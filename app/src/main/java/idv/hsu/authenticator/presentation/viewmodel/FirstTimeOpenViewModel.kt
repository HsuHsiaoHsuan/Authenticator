package idv.hsu.authenticator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import idv.hsu.authenticator.domain.GetFirstTimeOpenUseCase
import idv.hsu.authenticator.domain.SetFirstTimeOpenUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class FirstTimeOpenViewModel(
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
