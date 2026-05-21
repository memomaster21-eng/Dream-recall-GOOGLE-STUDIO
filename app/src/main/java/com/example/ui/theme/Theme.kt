package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DreamRecallColorScheme =
  darkColorScheme(
    primary = SoftCyan,
    onPrimary = NavyBackground,
    secondary = DimLavender,
    onSecondary = NavyBackground,
    tertiary = SoftCyanDark,
    background = NavyBackground,
    surface = NavySurface,
    surfaceVariant = NavySurfaceVariant,
    onBackground = WarmWhite,
    onSurface = WarmWhite,
    onSurfaceVariant = WarmWhiteMuted
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for the intended vibe
  dynamicColor: Boolean = false, // Disable dynamic colors to keep identity
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DreamRecallColorScheme, typography = Typography, content = content)
}
