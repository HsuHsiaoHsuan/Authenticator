package idv.hsu.authenticator.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import idv.hsu.authenticator.data.local.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val dataStore: DataStoreManager,
) {

    private object PreferencesKeys {
        val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
    }

    suspend fun saveFirstTimeStatus() {
        dataStore.getInstance().edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_TIME] = false
        }
    }

    suspend fun isFirstTime(): Boolean {
        return dataStore.getInstance().data.first()[PreferencesKeys.IS_FIRST_TIME] != false
    }

}