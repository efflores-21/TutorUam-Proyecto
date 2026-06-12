package com.example.tutoruam_proyecto.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AcademicLightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = OnPrimaryContainerBlue,
    inversePrimary = InversePrimaryBlue,
    secondary = SecondaryCool,
    onSecondary = OnSecondaryCool,
    secondaryContainer = SecondaryContainerCool,
    onSecondaryContainer = OnSecondaryContainerCool,
    tertiary = TertiaryNeutral,
    onTertiary = OnTertiaryNeutral,
    tertiaryContainer = TertiaryContainerNeutral,
    onTertiaryContainer = OnTertiaryContainerNeutral,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceContainerLowest,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineMuted,
    outlineVariant = OutlineVariantMuted,
    error = ErrorRed,
    onError = OnErrorWhite,
    errorContainer = ErrorContainerRed,
    onErrorContainer = OnErrorContainerRed
)

@Composable
fun TutorUamProyectoTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = AcademicLightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
