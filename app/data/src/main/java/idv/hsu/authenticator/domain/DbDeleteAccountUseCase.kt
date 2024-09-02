package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import javax.inject.Inject

class DbDeleteAccountUseCase @Inject constructor(
    private val repository: TotpRepository
) {
    suspend operator fun invoke(accountName: String) = repository.deleteAccountByName(accountName)
}