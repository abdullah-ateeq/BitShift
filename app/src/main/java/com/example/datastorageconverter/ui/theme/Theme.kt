package com.example.datastorageconverter.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.datastorageconverter.data.model.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueDark,
    onPrimary = Slate950,
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = SkyBlueLight,
    secondary = AccentTealDark,
    onSecondary = Slate950,
    secondaryContainer = Color(0xFF134E4A),
    onSecondaryContainer = LightTeal,
    tertiary = Slate400,
    onTertiary = Slate950,
    background = DarkBg,
    onBackground = Slate100,
    surface = DarkSurface,
    onSurface = Slate100,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Slate400,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = OceanBlue,
    onPrimary = PureWhite,
    primaryContainer = SkyBlueLight,
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = DeepTeal,
    onSecondary = PureWhite,
    secondaryContainer = LightTeal,
    onSecondaryContainer = Color(0xFF115E59),
    tertiary = Slate600,
    onTertiary = PureWhite,
    background = Slate50,
    onBackground = Slate900,
    surface = PureWhite,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate300,
    outlineVariant = Slate200
)

@Composable
fun DataStorageConverterTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (appTheme) {
        AppTheme.SYSTEM -> systemInDark
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}