package idv.hsu.authenticator.presentation.utils

import android.content.Context

object PreferencesManager {
    private const val PREF_NAME = "authenticator_prefs"
    private const val KEY_FIRST_TIME = "is_first_time"

    fun isFirstTimeLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FIRST_TIME, true)
    }

    fun setFirstTimeLaunch(context: Context, isFirstTime: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_TIME, isFirstTime).apply()
    }
}