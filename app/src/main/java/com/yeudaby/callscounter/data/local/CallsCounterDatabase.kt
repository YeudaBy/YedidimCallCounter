package com.yeudaby.callscounter.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AppSettingsEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CallsCounterDatabase : RoomDatabase() {

    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var instance: CallsCounterDatabase? = null

        fun getInstance(context: Context): CallsCounterDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CallsCounterDatabase::class.java,
                    "calls_counter.db",
                ).build().also { instance = it }
            }
        }
    }
}
