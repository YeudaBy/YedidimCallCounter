package com.yeudaby.callscounter.data.repository

import android.content.Context
import com.yeudaby.callscounter.data.local.AppSettingsEntity
import com.yeudaby.callscounter.data.local.CallsCounterDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class AppSettingsRepository private constructor(
    private val database: CallsCounterDatabase,
) {

    fun observeWeekGoal(): Flow<Int> {
        return database.appSettingsDao()
            .observeSettings()
            .map { settings -> (settings?.weekGoal ?: DEFAULT_WEEK_GOAL).coerceAtLeast(1) }
            .distinctUntilChanged()
    }

    suspend fun setWeekGoal(goal: Int) {
        database.appSettingsDao().upsert(
            AppSettingsEntity(weekGoal = goal.coerceAtLeast(1))
        )
    }

    companion object {
        const val DEFAULT_WEEK_GOAL = 15

        @Volatile
        private var instance: AppSettingsRepository? = null

        fun getInstance(context: Context): AppSettingsRepository {
            return instance ?: synchronized(this) {
                instance ?: AppSettingsRepository(
                    database = CallsCounterDatabase.getInstance(context.applicationContext),
                ).also { instance = it }
            }
        }
    }
}
