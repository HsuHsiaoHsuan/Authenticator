package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import idv.hsu.authenticator.data.local.TOTPAccount
import org.koin.core.annotation.Single

@Single
class InsertAccountUseCase(
    private val repository: TotpRepository
) {
    suspend operator fun invoke(account: TOTPAccount) = repository.insertAccount(account)
}