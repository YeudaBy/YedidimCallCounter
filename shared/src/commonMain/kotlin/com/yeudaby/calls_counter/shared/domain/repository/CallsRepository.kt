package com.yeudaby.calls_counter.shared.domain.repository

import com.yeudaby.calls_counter.shared.core.error.DataError
import com.yeudaby.calls_counter.shared.core.result.Result
import com.yeudaby.calls_counter.shared.domain.model.CallRecord
import kotlinx.coroutines.flow.Flow

interface CallsRepository {
    fun getCallRecordsFlow(fromTimestamp: Long): Flow<List<CallRecord>>
    suspend fun syncCallRecords(records: List<CallRecord>): Result<Unit, DataError>
}
