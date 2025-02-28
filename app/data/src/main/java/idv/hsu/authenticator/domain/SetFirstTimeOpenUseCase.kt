package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.DataStorePreferences
import javax.inject.Inject

class SetFirstTimeOpenUseCase @Inject constructor(
    private val preferences: DataStorePreferences
) {
    suspend operator fun invoke(isFirstTime: Boolean) {
        preferences.setFirstTimeOpen(isFirstTime)
    }
}