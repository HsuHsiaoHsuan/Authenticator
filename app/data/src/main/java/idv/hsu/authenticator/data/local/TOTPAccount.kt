package idv.hsu.authenticator.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "totp_accounts")
data class TOTPAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val accountName: String,
    val secret: String,
    val issuer: String?,
)