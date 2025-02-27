package idv.hsu.authenticator.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor() {
    private lateinit var dataStore: DataStore<Preferences>

    fun init(context: Context) {
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("authenticator_prefs") }
        )
    }

    fun getInstance(): DataStore<Preferences> {
        if (!::dataStore.isInitialized) {
            throw IllegalStateException("DataStore not initialized")
        }
        return dataStore
    }

    companion object {
        @Volatile
        private var instance: DataStoreManager? = null

        fun getInstance(): DataStoreManager =
            instance ?: synchronized(this) {
                instance ?: DataStoreManager().also { instance = it }
            }
    }
}