package idv.hsu.authenticator.data.local

import androidx.room.Entity

@Entity(
    tableName = "totp_accounts",
    primaryKeys = ["issuer", "accountName"]
)
data class TOTPAccount(
    val issuer: String = "",
    val accountName: String,
    val secret: String,
)