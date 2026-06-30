package idv.hsu.authenticator.data

import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.data.local.TOTPAccountDao
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class TotpRepository (
    private val totpAccountDao: TOTPAccountDao
) {

    suspend fun insertAccount(account: TOTPAccount): Long {
        return totpAccountDao.insertAccount(account)
    }

    suspend fun getAccount(issuer: String, accountName: String): TOTPAccount? {
        return totpAccountDao.getAccount(issuer, accountName)
    }

    fun getAllAccounts(): Flow<List<TOTPAccount>> {
        return totpAccountDao.getAllAccounts()
    }

    suspend fun deleteAccount(issuer: String, accountName: String): Int {
        return totpAccountDao.deleteAccount(issuer, accountName)
    }
}
