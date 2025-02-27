package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.DataStoreRepository
import javax.inject.Inject

class DsSetFirstTimeStatusUseCase @Inject constructor(
    private val repository: DataStoreRepository
) {
    suspend operator fun invoke() {
        repository.saveFirstTimeStatus()
    }
}