package com.yeudaby.callscounter.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = SETTINGS_ROW_ID,
    val weekGoal: Int,

) {
    companion object {
        const val SETTINGS_ROW_ID = 1
    }
}
