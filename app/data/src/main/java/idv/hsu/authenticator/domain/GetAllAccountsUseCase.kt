package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.TotpRepository
import idv.hsu.authenticator.data.local.TOTPAccount
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class GetAllAccountsUseCase(
    private val repository: TotpRepository
) {
    operator fun invoke(): Flow<List<TOTPAccount>> = repository.getAllAccounts()
}