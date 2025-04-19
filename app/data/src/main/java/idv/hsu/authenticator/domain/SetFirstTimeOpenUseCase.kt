package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.DataStorePreferences
import org.koin.core.annotation.Single

@Single
class SetFirstTimeOpenUseCase(
    private val preferences: DataStorePreferences
) {
    suspend operator fun invoke(isFirstTime: Boolean) {
        preferences.setFirstTimeOpen(isFirstTime)
    }
}