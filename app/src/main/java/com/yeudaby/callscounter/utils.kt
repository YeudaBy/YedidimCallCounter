package com.yeudaby.callscounter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.Settings

fun Cursor.safeGetColumnIndex(columnName: String): Int {
    val index = getColumnIndex(columnName)
    return if (index == -1) {
        getColumnIndexOrThrow(columnName)
    } else {
        index
    }
}

fun Cursor.getStringOrEmpty(columnIndex: Int): String {
    if (columnIndex == -1 || isNull(columnIndex)) return ""
    return getString(columnIndex).orEmpty()
}

fun Cursor.getLongOrDefault(columnIndex: Int, defaultValue: Long = 0L): Long {
    if (columnIndex == -1 || isNull(columnIndex)) return defaultValue
    return getLong(columnIndex)
}

fun Cursor.getIntOrDefault(columnIndex: Int, defaultValue: Int = 0): Int {
    if (columnIndex == -1 || isNull(columnIndex)) return defaultValue
    return getInt(columnIndex)
}

fun Context.exitApp() {
    // close the app
    (this as? android.app.Activity)?.finishAffinity()
}

fun Context.openAppSettings() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null),
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
