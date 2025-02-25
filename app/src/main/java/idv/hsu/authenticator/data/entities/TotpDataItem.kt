package idv.hsu.authenticator.data.entities

import idv.hsu.authenticator.R

data class TotpDataItem(
    val id: Int = 0,
    val accountName: String,
    val secret: String,
    val issuer: String?,
    val remainingTime: Long
) {
    fun getIssuerIcon(isDarkTheme: Boolean = false): Int {
        return when (issuer?.lowercase()) {
            "adobe" -> R.drawable.logo_adobe
            "apple" -> if (isDarkTheme) R.drawable.logo_apple_dark else R.drawable.logo_apple_light
            "dropbox" -> R.drawable.logo_dropbox
            "facebook" -> R.drawable.logo_facebook
            "figma" -> R.drawable.logo_figma
            "github" -> if (isDarkTheme) R.drawable.logo_github_dark else R.drawable.logo_github_light
            "google" -> R.drawable.logo_google
            "instagram" -> if (isDarkTheme) R.drawable.logo_instagram_dark else R.drawable.logo_instagram_light
            "linkedin" -> R.drawable.logo_linkedin
            "microsoft" -> R.drawable.logo_microsoft
            "netflix" -> R.drawable.logo_netflix
            "notion" -> R.drawable.logo_notion
            "spotify" -> R.drawable.logo_spotify
            "steam" -> R.drawable.logo_steam
            "telegram" -> R.drawable.logo_telegram
            "threads" -> if (isDarkTheme) R.drawable.logo_threads_dark else R.drawable.logo_threads_light
            "x" -> if (isDarkTheme) R.drawable.logo_x_dark else R.drawable.logo_x_light
            else -> R.drawable.ic_launcher_foreground
        }
    }
}