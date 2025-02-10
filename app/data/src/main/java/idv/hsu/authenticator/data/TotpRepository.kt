package idv.hsu.authenticator.data

import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.data.local.TOTPAccountDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TotpRepository @Inject constructor(
    private val totpAccountDao: TOTPAccountDao
) {

    suspend fun insertAccount(account: TOTPAccount): Long {
        return totpAccountDao.insertAccount(account)
    }

    suspend fun getAccountByName(accountName: String): TOTPAccount? {
        return totpAccountDao.getAccountByName(accountName)
    }

    fun getAllAccounts(): Flow<List<TOTPAccount>> {
        return totpAccountDao.getAllAccounts()
    }

    suspend fun deleteAccountByName(accountName: String): Int {
        return totpAccountDao.deleteAccountByName(accountName)
    }
}