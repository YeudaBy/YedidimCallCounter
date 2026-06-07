package com.yeudaby.callscounter.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yeudaby.calls_counter.shared.core.error.DataError
import com.yeudaby.calls_counter.shared.core.result.Result
import com.yeudaby.calls_counter.shared.domain.model.Region
import com.yeudaby.calls_counter.shared.domain.model.Role
import com.yeudaby.calls_counter.shared.domain.model.User
import com.yeudaby.calls_counter.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class AndroidUserRepositoryImpl(private val context: Context) : UserRepository {
    
    private val USER_ID = stringPreferencesKey("user_id")
    private val USER_NAME = stringPreferencesKey("user_name")
    private val USER_REGION = stringPreferencesKey("user_region")
    private val USER_DISPATCHER = stringPreferencesKey("user_dispatcher")
    private val USER_ROLE = stringPreferencesKey("user_role")
    private val USER_WEEKLY_GOAL = intPreferencesKey("user_weekly_goal")
    private val USER_IS_ANONYMOUS = booleanPreferencesKey("user_is_anonymous")

    override suspend fun saveUser(user: User): Result<Unit, DataError> {
        return try {
            context.dataStore.edit { prefs ->
                prefs[USER_ID] = user.id
                prefs[USER_NAME] = user.name
                prefs[USER_REGION] = user.region.name
                prefs[USER_DISPATCHER] = user.dispatcherNumber
                prefs[USER_ROLE] = user.role.name
                prefs[USER_WEEKLY_GOAL] = user.weeklyGoal
                prefs[USER_IS_ANONYMOUS] = user.isAnonymous
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun getUserFlow(): Flow<User?> {
        return context.dataStore.data.map { prefs ->
            val id = prefs[USER_ID] ?: return@map null
            val name = prefs[USER_NAME] ?: ""
            val region = Region.valueOf(prefs[USER_REGION] ?: Region.CENTER.name)
            val dispatcher = prefs[USER_DISPATCHER] ?: ""
            val role = Role.valueOf(prefs[USER_ROLE] ?: Role.REGULAR.name)
            val weeklyGoal = prefs[USER_WEEKLY_GOAL] ?: 40
            val isAnonymous = prefs[USER_IS_ANONYMOUS] ?: false
            User(id, name, region, dispatcher, role, null, weeklyGoal, isAnonymous)
        }
    }

    override suspend fun getUser(): Result<User, DataError> {
        val user = getUserFlow().firstOrNull()
        return if (user != null) {
            Result.Success(user)
        } else {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
