package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import javax.inject.Inject

class DbGetAccountUseCase @Inject constructor(
    private val repository: TotpRepository
) {
    suspend operator fun invoke(accountName: String) = repository.getAccountByName(accountName)
}