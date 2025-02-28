package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val repository: TotpRepository
) {
    suspend operator fun invoke(accountName: String) = repository.getAccountByName(accountName)
}