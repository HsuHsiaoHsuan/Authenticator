package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import org.koin.core.annotation.Single

@Single
class GetAccountUseCase(
    private val repository: TotpRepository
) {
    suspend operator fun invoke(issuer: String, accountName: String) =
        repository.getAccount(issuer, accountName)
}
