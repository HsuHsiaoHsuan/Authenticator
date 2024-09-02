package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import javax.inject.Inject

class DbGetAllAccountsUseCase @Inject constructor(
    private val totpRepository: TotpRepository
) {
    suspend operator fun invoke() = totpRepository.getAllAccounts()
}