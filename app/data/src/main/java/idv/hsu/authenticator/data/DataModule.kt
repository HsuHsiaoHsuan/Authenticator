package idv.hsu.authenticator.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import idv.hsu.authenticator.data.local.TOTPAccountDao
import idv.hsu.authenticator.data.local.TOTPDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) : TOTPDatabase {
        return Room.databaseBuilder(
            appContext,
            TOTPDatabase::class.java,
            "totp_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(database: TOTPDatabase) = database.totpAccountDao()

    @Provides
    @Singleton
    fun provideRepository(totpAccountDao: TOTPAccountDao) = TotpRepository(totpAccountDao)
}