package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.DataStorePreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFirstTimeOpenUseCase @Inject constructor(
    private val preferences: DataStorePreferences
) {
    operator fun invoke(): Flow<Boolean> = preferences.isFirstTimeOpen
}