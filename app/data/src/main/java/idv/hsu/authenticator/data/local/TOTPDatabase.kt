package idv.hsu.authenticator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TOTPAccount::class], version = 1)
abstract class TOTPDatabase : RoomDatabase() {
    abstract fun totpAccountDao(): TOTPAccountDao
}