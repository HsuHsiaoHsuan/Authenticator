package idv.hsu.authenticator.data

import android.content.Context
import androidx.room.Room
import idv.hsu.authenticator.data.local.TOTPAccountDao
import idv.hsu.authenticator.data.local.TOTPDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DataModule {
    @Single
    fun provideDatabase(context: Context): TOTPDatabase {
        return Room.databaseBuilder(
            context,
            TOTPDatabase::class.java,
            "totp_database"
        ).build()
    }

    @Single
    fun provideDao(database: TOTPDatabase): TOTPAccountDao = database.totpAccountDao()

    @Single
    fun provideRepository(totpAccountDao: TOTPAccountDao): TotpRepository =
        TotpRepository(totpAccountDao)
}