package idv.hsu.authenticator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TOTPAccountDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(account: TOTPAccount): Long

    @Query("SELECT * FROM totp_accounts WHERE issuer = :issuer AND accountName = :accountName")
    suspend fun getAccount(issuer: String, accountName: String): TOTPAccount?

    @Query("SELECT * FROM totp_accounts")
    fun getAllAccounts(): Flow<List<TOTPAccount>>

    @Query("DELETE FROM totp_accounts WHERE issuer = :issuer AND accountName = :accountName")
    suspend fun deleteAccount(issuer: String, accountName: String): Int
}
