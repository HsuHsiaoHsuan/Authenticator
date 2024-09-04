package idv.hsu.authenticator.data

import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.data.local.TOTPAccountDao
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

    suspend fun getAllAccounts(): List<TOTPAccount> {
        return totpAccountDao.getAllAccounts()
    }

    suspend fun deleteAccountByName(accountName: String): Int {
        return totpAccountDao.deleteAccountByName(accountName)
    }
}