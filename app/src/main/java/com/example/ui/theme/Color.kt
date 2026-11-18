package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class DiriddlePalette(
    val background: Color = Color(0xFF090B0F),
    val surface: Color = Color(0xFF12161D),
    val card: Color = Color(0xFF191F28),
    val cardElevated: Color = Color(0xFF1F2631),
    val textPrimary: Color = Color(0xFFF5F7FA),
    val textSecondary: Color = Color(0xFF929BA8),
    val textMuted: Color = Color(0xFF636D7A),
    val accent: Color = Color(0xFFFF8A45),
    val accentMuted: Color = Color(0x29FF8A45),
    val accentBorder: Color = Color(0x59FF8A45),
    val border: Color = Color(0xFF262D37),
    val borderSubtle: Color = Color(0xFF1B212A),
    val success: Color = Color(0xFF34D399),
    val successMuted: Color = Color(0x2634D399),
    val warning: Color = Color(0xFFFBBF24),
    val warningMuted: Color = Color(0x26FBBF24),
    val danger: Color = Color(0xFFF87171),
    val dangerMuted: Color = Color(0x26F87171),
    val info: Color = Color(0xFF60A5FA),
    val infoMuted: Color = Color(0x2660A5FA),
    val overlay: Color = Color(0xD1090B0F),
)

val LocalDiriddlePalette = staticCompositionLocalOf { DiriddlePalette() }
