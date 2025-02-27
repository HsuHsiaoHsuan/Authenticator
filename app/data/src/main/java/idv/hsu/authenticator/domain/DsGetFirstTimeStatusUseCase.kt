package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.DataStoreRepository
import javax.inject.Inject

class DsGetFirstTimeStatusUseCase @Inject constructor(
    private val repository: DataStoreRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isFirstTime()
    }
}