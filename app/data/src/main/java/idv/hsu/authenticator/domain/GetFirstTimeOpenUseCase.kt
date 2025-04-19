package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.DataStorePreferences
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class GetFirstTimeOpenUseCase(
    private val preferences: DataStorePreferences
) {
    operator fun invoke(): Flow<Boolean> = preferences.isFirstTimeOpen
}