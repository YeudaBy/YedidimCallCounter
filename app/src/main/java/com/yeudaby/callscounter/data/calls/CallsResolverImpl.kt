package com.yeudaby.callscounter.data.calls

import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.provider.CallLog.Calls.DEFAULT_SORT_ORDER
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CallsResolverImpl(private val context: Context) : CallsResolver {

    override suspend fun queryCalls(
        fromDateMillis: Long, toDateMillis: Long
    ): List<CallLogEntry> {  // todo handle errors
        val selection = getSelection(fromDateMillis, toDateMillis)
        val selectionArgs = getSelectionArgs(fromDateMillis, toDateMillis)
        return queryCalls(selection, selectionArgs)
    }

    private suspend fun queryCalls(
        selection: String?,
        selectionArgs: Array<String> = emptyArray<String>(),
        sortOrder: String = DEFAULT_SORT_ORDER
    ): List<CallLogEntry> = withContext(Dispatchers.IO) {
        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, sortOrder, null
        ).use { cursor ->
            if (cursor == null) return@withContext emptyList()

            val results = mutableListOf<CallLogEntry>()
            while (cursor.moveToNext()) {
                results += cursor.getCallLogEntry()
            }
            return@withContext results
        }
    }

    private fun getSelection(fromDateMillis: Long, toDateMillis: Long): String {
        return "${CallLog.Calls.DATE} >= ? AND ${CallLog.Calls.DATE} <= ? AND ($TARGET_NUMBERS_SELECTION)"
    }

    private fun getSelectionArgs(fromDateMillis: Long, toDateMillis: Long): Array<String> {
        return arrayOf(fromDateMillis.toString(), toDateMillis.toString())
    }

    companion object {
        private val projection = arrayOf(
            CallLog.Calls.DATE,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE,
        )

        private val targetNumbers = listOf(
            "1230", "0533131310"
        )
        private val TARGET_NUMBERS_SELECTION = targetNumbers.joinToString(" OR ") {
            "${CallLog.Calls.NUMBER} LIKE '%$it%'"
        }
    }

    private fun Cursor.getCallLogEntry(): CallLogEntry {
        val dateIdx = getColumnIndex(CallLog.Calls.DATE)
        val numberIdx = getColumnIndex(CallLog.Calls.NUMBER)
        val durationIdx = getColumnIndex(CallLog.Calls.DURATION)
        val typeIdx = getColumnIndex(CallLog.Calls.TYPE)
        return CallLogEntry(
            date = getLong(dateIdx),
            number = getString(numberIdx),
            duration = getLong(durationIdx).toDuration(DurationUnit.SECONDS),
            type = intToCallType(typeIdx)
        )
    }

    private fun intToCallType(type: Int): CallType {
        return when (type) {
            CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
            CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
            else -> CallType.MISSED
        }
    }
}