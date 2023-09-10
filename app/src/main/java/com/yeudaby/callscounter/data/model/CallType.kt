package com.yeudaby.callscounter.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.yeudaby.callscounter.R

enum class CallType(
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val color: Color
) {
    INCOMING(
        iconRes = R.drawable.baseline_phone_callback_24,
        titleRes = R.string.incoming,
        color = Color(0xFF4CAF50),
    ),
    OUTGOING(
        iconRes = R.drawable.baseline_call_end_24,
        titleRes = R.string.outgoing,
        color = Color(0xFF03A9F4),
    ),
    MISSED(
        iconRes = R.drawable.baseline_call_missed_outgoing_24,
        titleRes = R.string.missed,
        color = Color(0xFFE91E63),
    ),
}