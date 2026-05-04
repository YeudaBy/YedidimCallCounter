package com.yeudaby.callscounter.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Full semantic color palette for the app.
 * To reskin: change [accent] in [CallsCounterTheme] — everything else adapts automatically.
 */
data class AppColors(
    val accent: Color,
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val border: Color,
    val text: Color,
    val muted: Color,
    val incoming: Color,
    val outgoing: Color,
    val missed: Color,
    val amber: Color,
) {
    fun cardTint(color: Color)   = color.copy(alpha = 0.07f)
    fun cardBorder(color: Color) = color.copy(alpha = 0.22f)
}

// ── Accent presets – change the argument in Theme.kt to reskin ──────────────
val AccentPurple = Color(0xFFa485e0)
val AccentAmber  = Color(0xFFe0cc85)
val AccentTeal   = Color(0xFF4DD0C4)
val AccentRose   = Color(0xFFE091C8)

// ── Dark palette (matches HTML design) ──────────────────────────────────────
fun darkAppColors(accent: Color = AccentPurple) = AppColors(
    accent     = accent,
    background = Color(0xFF0E0E14),
    surface    = Color(0xFF1A1A24),
    surface2   = Color(0xFF22222F),
    border     = Color(0xFF2E2E40),
    text       = Color(0xFFE8E8F0),
    muted      = Color(0xFF888899),
    incoming   = Color(0xFF66BB6A),
    outgoing   = Color(0xFF4DD0C4),
    missed     = Color(0xFFEF5350),
    amber      = Color(0xFFFFB300),
)

// ── Light palette ────────────────────────────────────────────────────────────
fun lightAppColors(accent: Color = Color(0xFF7B5EA7)) = AppColors(
    accent     = accent,
    background = Color(0xFFFAF8FD),
    surface    = Color(0xFFFFFFFF),
    surface2   = Color(0xFFF3EEF8),
    border     = Color(0xFFE2D9EE),
    text       = Color(0xFF1A1A2E),
    muted      = Color(0xFF666688),
    incoming   = Color(0xFF2E7D32),
    outgoing   = Color(0xFF00796B),
    missed     = Color(0xFFC62828),
    amber      = Color(0xFFE65100),
)

val LocalAppColors = staticCompositionLocalOf { darkAppColors() }
