package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import idv.hsu.authenticator.data.local.TOTPAccount
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DbGetAllAccountsUseCase @Inject constructor(
    private val totpRepository: TotpRepository
) {
    operator fun invoke(): Flow<List<TOTPAccount>> = totpRepository.getAllAccounts()
}