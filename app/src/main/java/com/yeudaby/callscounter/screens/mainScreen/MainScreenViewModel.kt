package com.yeudaby.callscounter.screens.mainScreen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.provider.CallLog
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeudaby.callscounter.R
import com.yeudaby.callscounter.data.model.CallLogEntry
import com.yeudaby.callscounter.data.model.CallType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class MainScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState(
        filteredCalls = emptyList(),
        withDuration = false,
        selectedCallTypes = listOf(CallType.INCOMING),
        calls = emptyList(),
        data = emptyList(),
        statistics = null
    ))
    val uiState get() = _uiState

    fun init(context: Context) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            calls = emptyList(),
            filteredCalls = emptyList(),
        )
        getCallsLog(
            fromDateMillis = getMonthsAgo(1),
            toDateMillis = System.currentTimeMillis(),
            context = context,
        )
        _uiState.value = _uiState.value.copy(
            statistics = getStatistics(),
            data = getData(),
        )
    }

    fun onDurationCheckedChange(
        isChecked: Boolean,
    ) {
        _uiState.value = _uiState.value.copy(
            withDuration = isChecked,
        )
        filterCalls()
    }

    fun onCallTypeCheckedChange(
        callType: CallType
    ) {
        val currentSelectedCallTypes = _uiState.value.selectedCallTypes
        if (!currentSelectedCallTypes.contains(callType)) {
            _uiState.value = _uiState.value.copy(
                selectedCallTypes = currentSelectedCallTypes.plus(callType),
            )
        } else {
            _uiState.value = _uiState.value.copy(
                selectedCallTypes = currentSelectedCallTypes.minus(callType),
            )
        }
        filterCalls()
    }

    private fun getData(): List<DataItem> {
        return listOf(
            DataItem(
                label = R.string.start_of_hour,
                count = filterByDate(getFromStartOfTheHour()).size,
                color = statsColors["hour"]!!,
                fromMillis = getFromStartOfTheHour(),
            ),
            DataItem(
                label = R.string.last_hour,
                count = filterByDate(getHoursAgo(1)).size,
                color = statsColors["hour"]!!,
                fromMillis = getHoursAgo(1),
            ),
            DataItem(
                label = R.string.start_of_day,
                count = filterByDate(getFromStartOfTheDay()).size,
                color = statsColors["day"]!!,
                fromMillis = getFromStartOfTheDay(),
            ),
            DataItem(
                label = R.string.last_day,
                count = filterByDate(getDaysAgo(1)).size,
                color = statsColors["day"]!!,
                fromMillis = getDaysAgo(1),
            ),
            DataItem(
                label = R.string.start_of_week,
                count = filterByDate(getFromStartOfTheWeek()).size,
                color = statsColors["week"]!!,
                fromMillis = getFromStartOfTheWeek(),
            ),
            DataItem(
                label = R.string.last_week,
                count = filterByDate(getWeeksAgo(1)).size,
                color = statsColors["week"]!!,
                fromMillis = getWeeksAgo(1),
            ),
            DataItem(
                label = R.string.start_of_month,
                count = filterByDate(getFromStartOfTheMonth()).size,
                color = statsColors["month"]!!,
                fromMillis = getFromStartOfTheMonth(),
            ),
            DataItem(
                label = R.string.last_month,
                count = filterByDate(getMonthsAgo(1)).size,
                color = statsColors["month"]!!,
                fromMillis = getMonthsAgo(1),
            )
        )
    }

    private fun getStatistics(): Statistics {
        val filteredCalls = _uiState.value.filteredCalls
        val longestCall = filteredCalls.maxByOrNull { it.duration }!!
        val mostBusiestHour = filteredCalls.groupBy {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }.maxByOrNull { it.value.size }!!.key
        val totalDurationStartOfHour = filteredCalls.filter {
            it.date >= getFromStartOfTheHour()
        }.sumBy { it.duration.toInt() }
        val totalDurationStartOfDay = filteredCalls.filter {
            it.date >= getFromStartOfTheDay()
        }.sumBy { it.duration.toInt() }
        val totalDurationStartOfWeek = filteredCalls.filter {
            it.date >= getFromStartOfTheWeek()
        }.sumBy { it.duration.toInt() }
        val totalDurationStartOfMonth = filteredCalls.filter {
            it.date >= getFromStartOfTheMonth()
        }.sumBy { it.duration.toInt() }
        return Statistics(
            longestCall = longestCall,
            mostBusiestHour = mostBusiestHour,
            totalDurationStartOfHour = totalDurationStartOfHour,
            totalDurationStartOfDay = totalDurationStartOfDay,
            totalDurationStartOfWeek = totalDurationStartOfWeek,
            totalDurationStartOfMonth = totalDurationStartOfMonth,
        )
    }

    data class Statistics(
        val longestCall: CallLogEntry,
        val mostBusiestHour: Long,
        val totalDurationStartOfHour: Int,
        val totalDurationStartOfDay: Int,
        val totalDurationStartOfWeek: Int,
        val totalDurationStartOfMonth: Int,
    )

    private val statsColors = mapOf<String, Color>(
        "hour" to Color(0xFF9C27B0),
        "day" to Color(0xFFFF5722),
        "week" to Color(0xFF4CAF50),
        "month" to Color(0xFFFFEB3B),
    )

    private fun filterCalls() {
        val filteredCalls = _uiState.value.calls.filter { isValidCall(it) }
        _uiState.value = _uiState.value.copy(
            filteredCalls = filteredCalls,
        )
    }

    private fun getCallsLog(
        fromDateMillis: Long,
        toDateMillis: Long,
        context: Context,
    ) {
        val contentResolver: ContentResolver = context.contentResolver
        val selection =
            "${CallLog.Calls.DATE} >= ? AND ${CallLog.Calls.DATE} <= ?"
        val selectionArgs =
            arrayOf(fromDateMillis.toString(), toDateMillis.toString())
        val sortOrder = "${CallLog.Calls.DATE} DESC"
        val projection = arrayOf(
            CallLog.Calls.DATE,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE,
        )
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))
                val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                val duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION))
                val type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
                val callType = when (type) {
                    CallLog.Calls.INCOMING_TYPE -> CallType.INCOMING
                    CallLog.Calls.OUTGOING_TYPE -> CallType.OUTGOING
                    else -> CallType.MISSED
                }

                val newCallLog = CallLogEntry(
                    date = date,
                    number = number,
                    duration = duration,
                    type = callType,
                )

                _uiState.value = _uiState.value.copy(
                    calls = _uiState.value.calls.plus(
                        newCallLog.also {
//                            i("CallLogEntry: $it")
                        }
                    )
                )
            }
            cursor.close()
        }
        filterCalls()
    }

    private fun isValidCall(
        callLogEntry: CallLogEntry,
    ): Boolean {
        val withDuration = _uiState.value.withDuration
        val selectedCallTypes = _uiState.value.selectedCallTypes
        return selectedCallTypes.contains(callLogEntry.type) && if (withDuration) {
            callLogEntry.duration >= 15
        } else {
            true
        } && callLogEntry.number in listOf("1230", "0533131310")
    }


    private fun getHoursAgo(hoursAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, -hoursAgo)
        return calendar.timeInMillis
    }

    private fun getFromStartOfTheHour(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }

    private fun getDaysAgo(daysAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.timeInMillis
    }

    private fun getFromStartOfTheDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        return calendar.timeInMillis
    }

    private fun getWeeksAgo(weeksAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -weeksAgo)
        return calendar.timeInMillis
    }

    private fun getFromStartOfTheWeek(): Long {
        val calendar = Calendar.getInstance()

        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysToSubtract = (currentDayOfWeek - Calendar.FRIDAY + 7) % 7
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract)

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }


    private fun getMonthsAgo(monthsAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -monthsAgo)
        return calendar.timeInMillis
    }

    private fun getFromStartOfTheMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        return calendar.timeInMillis
    }

    private fun filterByDate(
        date: Long,
    ): List<CallLogEntry> {
        return _uiState.value.filteredCalls.filter {
            it.date >= date
        }
    }

    data class DataItem(
        val count: Int,
        @StringRes val label: Int,
        val color: Color,
        val fromMillis: Long,
    ) {
        fun toImageBitmap(context: Context): ImageBitmap {
            val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint()

            paint.isFilterBitmap = false
            paint.isDither = true

            paint.color = color.copy(alpha = 0.2f).toArgb()
            canvas.drawRect(0f, 0f, 1800f, 1800f, paint)

            paint.textAlign = Paint.Align.CENTER
            paint.style = Paint.Style.FILL
            paint.isAntiAlias = true

            paint.color = Color.White.toArgb()
            paint.textSize = 80f
            paint.isFakeBoldText = true

            canvas.drawText(
                count.toString(),
                150f,
                150f,
                paint
            )

            paint.textSize = 25f
            paint.isFakeBoldText = false
            paint.letterSpacing = 0.1f

            canvas.drawText(
                context.getString(R.string.calls),
                150f,
                200f,
                paint
            )

            paint.isFakeBoldText = true
            paint.textSize = 20f
            paint.color = color.toArgb()

            canvas.drawText(
                context.getString(label),
                150f,
                225f,
                paint
            )

            paint.isAntiAlias = true

            val icon = AppCompatResources.getDrawable(context, R.drawable.horizontal_logo)
            icon?.setBounds(10, 10, 150, 65)
            icon?.draw(canvas)

            return bitmap.asImageBitmap()
        }
    }

}