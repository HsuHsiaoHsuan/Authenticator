package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import idv.hsu.authenticator.data.local.TOTPAccount
import javax.inject.Inject

class DbInsertAccountUseCase @Inject constructor(
    private val repository: TotpRepository
) {
    suspend operator fun invoke(account: TOTPAccount) = repository.insertAccount(account)
}