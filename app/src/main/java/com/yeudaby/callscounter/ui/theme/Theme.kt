package com.yeudaby.callscounter.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.core.view.WindowCompat
import com.yeudaby.callscounter.R

val gfProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

// ── Font families ─────────────────────────────────────────────────────────────
val InstrumentSerif = FontFamily(
    Font(GoogleFont("Instrument Serif"), fontProvider = gfProvider),
    Font(GoogleFont("Instrument Serif"), fontProvider = gfProvider, style = FontStyle.Italic),
)

val DmSans = FontFamily(
    Font(GoogleFont("DM Sans"), fontProvider = gfProvider),
)

// Kept for any external usage
val Tinos = FontFamily(
    Font(GoogleFont("Tinos"), fontProvider = gfProvider),
)

val CustomTypography = Typography(
    bodyLarge      = Typography().bodyLarge.copy(fontFamily = DmSans),
    bodyMedium     = Typography().bodyMedium.copy(fontFamily = DmSans),
    bodySmall      = Typography().bodySmall.copy(fontFamily = DmSans),
    headlineLarge  = Typography().headlineLarge.copy(fontFamily = DmSans),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = DmSans),
    headlineSmall  = Typography().headlineSmall.copy(fontFamily = DmSans),
    titleLarge     = Typography().titleLarge.copy(fontFamily = DmSans),
    titleMedium    = Typography().titleMedium.copy(fontFamily = DmSans),
    titleSmall     = Typography().titleSmall.copy(fontFamily = DmSans),
    labelLarge     = Typography().labelLarge.copy(fontFamily = DmSans),
    labelMedium    = Typography().labelMedium.copy(fontFamily = DmSans),
    labelSmall     = Typography().labelSmall.copy(fontFamily = DmSans),
)

/**
 * Main app theme.
 *
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  To fully reskin the app, change [accent] to any Color.         │
 * │  Examples: AccentPurple (default), AccentAmber, AccentTeal …    │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Composable
fun CallsCounterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: Color = AccentTeal,
    content: @Composable () -> Unit,
) {
    val appColors = if (darkTheme) darkAppColors(accent) else lightAppColors(accent)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary          = appColors.accent,
            background       = appColors.background,
            surface          = appColors.surface,
            surfaceVariant   = appColors.surface2,
            outline          = appColors.border,
            onBackground     = appColors.text,
            onSurface        = appColors.text,
            onSurfaceVariant = appColors.muted,
            error            = appColors.missed,
        )
    } else {
        lightColorScheme(
            primary          = appColors.accent,
            background       = appColors.background,
            surface          = appColors.surface,
            surfaceVariant   = appColors.surface2,
            outline          = appColors.border,
            onBackground     = appColors.text,
            onSurface        = appColors.text,
            onSurfaceVariant = appColors.muted,
            error            = appColors.missed,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = appColors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = CustomTypography,
            content     = content,
        )
    }
}
