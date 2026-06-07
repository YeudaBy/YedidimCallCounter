package com.yeudaby.calls_counter.shared.domain.repository

import com.yeudaby.calls_counter.shared.core.error.DataError
import com.yeudaby.calls_counter.shared.core.result.Result
import com.yeudaby.calls_counter.shared.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit, DataError>
    fun getUserFlow(): Flow<User?>
    suspend fun getUser(): Result<User, DataError>
}
