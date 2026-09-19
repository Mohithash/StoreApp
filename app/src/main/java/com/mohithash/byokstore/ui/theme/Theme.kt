@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.mohithash.byokstore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

private fun Color.hsl(): FloatArray {
    val r = red; val g = green; val b = blue
    val max = maxOf(r, g, b); val min = minOf(r, g, b); val l = (max + min) / 2
    if (max == min) return floatArrayOf(0f, 0f, l)
    val d = max - min
    val s = if (l > 0.5f) d / (2 - max - min) else d / (max + min)
    val h = when (max) { r -> ((g - b) / d + (if (g < b) 6 else 0)) / 6; g -> ((b - r) / d + 2) / 6; else -> ((r - g) / d + 4) / 6 }
    return floatArrayOf(h, s, l)
}
private fun hslColor(h: Float, s: Float, l: Float): Color {
    fun hue(p: Float, q: Float, tt: Float): Float { var t = tt; if (t < 0) t += 1; if (t > 1) t -= 1
        return when { t < 1f / 6 -> p + (q - p) * 6 * t; t < 0.5f -> q; t < 2f / 3 -> p + (q - p) * (2f / 3 - t) * 6; else -> p } }
    if (s == 0f) return Color(l, l, l)
    val q = if (l < 0.5f) l * (1 + s) else l + s - l * s; val p = 2 * l - q
    return Color(hue(p, q, h + 1f / 3), hue(p, q, h), hue(p, q, h - 1f / 3))
}
/** Same tone(lightness) helper the factory uses, so store icons match the real launcher icons. */
fun Color.tone(l: Float, s: Float? = null): Color { val (h, ss, _) = hsl(); return hslColor(h, s ?: ss, l) }

val AppTypography = Typography().let { t ->
    t.copy(
        displayLarge = t.displayLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-2).sp),
        displayMedium = t.displayMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-1.5).sp),
        displaySmall = t.displaySmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
        headlineLarge = t.headlineLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
        headlineMedium = t.headlineMedium.copy(fontWeight = FontWeight.Bold),
        headlineSmall = t.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = t.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = t.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = t.labelLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp),
    )
}

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialExpressiveTheme(
        colorScheme = if (darkTheme) Brand.dark else Brand.light,
        motionScheme = MotionScheme.expressive(),
        typography = AppTypography,
        content = content,
    )
}
