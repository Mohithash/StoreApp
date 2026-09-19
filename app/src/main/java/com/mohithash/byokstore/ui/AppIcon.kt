package com.mohithash.byokstore.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mohithash.byokstore.data.AppInfo
import com.mohithash.byokstore.ui.theme.tone

fun String.hex(): Color = runCatching { Color(("FF" + removePrefix("#")).toLong(16)) }.getOrDefault(Color(0xFF1B5E4A))

/** Draws the factory adaptive icon: gradient rounded square + glyph, from catalog data alone. */
@Composable
fun AppIcon(a: AppInfo, size: Dp = 56.dp) {
    val c0 = a.colors.getOrNull(0)?.hex() ?: Color(0xFF1B5E4A); val c1 = a.colors.getOrNull(1)?.hex() ?: c0
    val path = remember(a.icon) { PathParser().parsePathString(Glyphs.map[a.icon] ?: Glyphs.map["spark"]!!).toPath() }
    Canvas(Modifier.size(size)) {
        drawRoundRect(Brush.linearGradient(listOf(c0.tone(.42f), c0.tone(.20f))), cornerRadius = CornerRadius(this.size.width * 0.24f))
        // Same geometry as the adaptive icon: 108-unit viewport, glyph scaled 2.1× and offset 28.8.
        val unit = this.size.width / 108f
        translate(28.8f * unit, 28.8f * unit) { scale(2.1f * unit, 2.1f * unit, pivot = androidx.compose.ui.geometry.Offset.Zero) { drawPath(path, c1.tone(.85f)) } }
    }
}
