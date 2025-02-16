package idv.hsu.authenticator.data.entities

import idv.hsu.authenticator.R

data class TotpDataItem(
    val id: Int = 0,
    val accountName: String,
    val secret: String,
    val issuer: String?,
    val remainingTime: Long
) {
    fun getIssuerIcon(): Int {
        return issuerIcons[issuer?.lowercase()] ?: R.drawable.ic_launcher_foreground
    }
}

private val issuerIcons = mapOf(
    "google" to R.drawable.logo_google,
    "github" to R.drawable.logo_google,
    "microsoft" to R.drawable.logo_google,
    "facebook" to R.drawable.logo_google,
    "amazon" to R.drawable.logo_google,
    "dropbox" to R.drawable.logo_google,
    "twitter" to R.drawable.logo_google,
    "instagram" to R.drawable.logo_instagram
)