package com.yeudaby.callscounter

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import androidx.core.content.ContentResolverCompat





//object LogReader {
//
//    fun getIncomingCalls(
//        context: Context,
//        fromDateMillis: Long,
//        toDateMillis: Long,
//    ): Cursor? {
//        val contentResolver: ContentResolver = context.contentResolver
//        val selection =
//            "${CallLog.Calls.DATE} >= ? AND ${CallLog.Calls.DATE} <= ?"
//        val selectionArgs =
//            arrayOf(fromDateMillis.toString(), toDateMillis.toString())
//        val sortOrder = "${CallLog.Calls.DATE} DESC"
//        val projection = arrayOf(
//            CallLog.Calls.DATE,
//            CallLog.Calls.NUMBER,
//            CallLog.Calls.DURATION,
//            CallLog.Calls.TYPE,
//        )
//        return ContentResolverCompat.query(
//            contentResolver,
//            CallLog.Calls.CONTENT_URI,
//            projection,
//            selection,
//            selectionArgs,
//            sortOrder,
//            null
//        )
//    }
//
//}null