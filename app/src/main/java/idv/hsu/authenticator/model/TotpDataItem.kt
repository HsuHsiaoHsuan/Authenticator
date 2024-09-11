package idv.hsu.authenticator.model

data class TotpDataItem(
    val id: Int = 0,
    val accountName: String,
    val secret: String,
    val issuer: String?,
    val remainingTime: Long
)