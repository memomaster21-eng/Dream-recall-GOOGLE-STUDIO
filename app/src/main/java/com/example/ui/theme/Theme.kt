package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = SoftCyan,
    secondary = DimLavender,
    background = DeepBlack,
    surface = CardBackground,
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onBackground = WarmWhite,
    onSurface = WarmWhite,
    surfaceVariant = DeepNavy,
    onSurfaceVariant = GrayText
)

@Composable
fun DreamRecallTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            content()
        }
    }
}
