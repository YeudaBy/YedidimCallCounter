package com.yeudaby.callscounter.data.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import com.yeudaby.callscounter.R

data class DataItem(
    val count: Int,
    @StringRes val label: Int,
    val color: Color,
    val fromMillis: Long,
) {
    fun toImageBitmap(context: Context): ImageBitmap {
        val bitmap = Bitmap.createBitmap(1800, 1800, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        paint.isFilterBitmap = false
        paint.isDither = true

        paint.color = color.copy(alpha = 0.2f).toArgb()
        canvas.drawRect(0f, 0f, 1800f, 1800f, paint)

        paint.textAlign = Paint.Align.CENTER
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true

        // Increase font size by a factor of 4
        paint.color = Color.White.toArgb()
        paint.textSize = 320f // 80 * 4 = 320
        paint.isFakeBoldText = true

        // Draw the count text
        canvas.drawText(
            count.toString(),
            900f, // Center horizontally
            900f, // Center vertically
            paint
        )

        paint.textSize = 100f // 25 * 4 = 100
        paint.isFakeBoldText = false
        paint.letterSpacing = 0.1f

        // Draw the "calls" text
        canvas.drawText(
            context.getString(R.string.calls),
            900f, // Center horizontally
            1050f, // Adjusted to be below the count text
            paint
        )

        paint.isFakeBoldText = true
        paint.textSize = 80f // 20 * 4 = 80
        paint.color = color.toArgb()

        // Draw the label text
        canvas.drawText(
            context.getString(label),
            900f, // Center horizontally
            1200f, // Adjusted to be below the "calls" text
            paint
        )

        paint.isAntiAlias = true

        // Increase icon size by a factor of 4
        val icon = AppCompatResources.getDrawable(context, R.drawable.horizontal_logo)
        icon?.setBounds(40, 40, 600, 250) // 150 * 4 = 600, 65 * 4 = 250
        icon?.draw(canvas)

        return bitmap.asImageBitmap()
    }

}