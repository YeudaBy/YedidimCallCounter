package com.yeudaby.callscounter.data

import android.content.Context
import android.provider.CallLog
import com.yeudaby.calls_counter.shared.core.error.DataError
import com.yeudaby.calls_counter.shared.core.result.Result
import com.yeudaby.calls_counter.shared.domain.model.CallRecord
import com.yeudaby.calls_counter.shared.domain.model.CallType
import com.yeudaby.calls_counter.shared.domain.repository.CallsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AndroidCallsRepositoryImpl(private val context: Context) : CallsRepository {

    override fun getCallRecordsFlow(fromTimestamp: Long): Flow<List<CallRecord>> = flow {
        while (true) {
            val records = queryCallLogs(fromTimestamp)
            emit(records)
            delay(5000) // Poll every 5s. A ContentObserver could be more efficient but this is reliable.
        }
    }.flowOn(Dispatchers.IO)

    private fun queryCallLogs(fromTimestamp: Long): List<CallRecord> {
        val records = mutableListOf<CallRecord>()
        
        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(
                    CallLog.Calls._ID,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.TYPE
                ),
                "${CallLog.Calls.DATE} >= ? AND (${CallLog.Calls.NUMBER} = ? OR ${CallLog.Calls.NUMBER} = ?)",
                arrayOf(fromTimestamp.toString(), "1230", "0533131310"),
                "${CallLog.Calls.DATE} DESC"
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(CallLog.Calls._ID)
                val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
                val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)

                while (it.moveToNext()) {
                    val typeInt = it.getInt(typeIndex)
                    val callType = when (typeInt) {
                        CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
                        CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
                        CallLog.Calls.MISSED_TYPE -> CallType.MISSED
                        CallLog.Calls.REJECTED_TYPE -> CallType.REJECTED
                        else -> CallType.INCOMING
                    }

                    records.add(
                        CallRecord(
                            id = it.getString(idIndex),
                            timestamp = it.getLong(dateIndex),
                            durationSeconds = it.getInt(durationIndex),
                            type = callType,
                            number = it.getString(numberIndex)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return records
    }

    override suspend fun syncCallRecords(records: List<CallRecord>): Result<Unit, DataError> {
        // Implementation for sending summary to server.
        return Result.Success(Unit)
    }
}
