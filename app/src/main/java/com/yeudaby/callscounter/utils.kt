package com.yeudaby.callscounter

import android.database.Cursor

fun Cursor.safeGetColumnIndex(columnName: String): Int {
    val index = getColumnIndex(columnName)
    return if (index == -1) {
        getColumnIndexOrThrow(columnName)
    } else {
        index
    }
}

