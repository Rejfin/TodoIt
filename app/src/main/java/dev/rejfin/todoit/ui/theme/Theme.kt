package dev.rejfin.todoit.ui.theme

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color.Companion.Red


private val CustomLightColors = CustomColors(
    primaryColor = primaryColor,
    secondaryColor = secondaryColor,
    cardBackgroundColor = cardBackgroundColor,
    textColorFirst = textFirstColor,
    textColorSecond = textSecondColor,
    textColorThird = textThirdColor,
    textColorOnPrimary = textColorOnPrimary,
    appBackground = appBackground,
    cardBorderColor = cardBorderColor,
    doneColor = doneColor,
    errorColor = errorColor
)

private val CustomDarkColors = CustomColors(
    primaryColor = darkPrimaryColor,
    secondaryColor = darkSecondaryColor,
    cardBackgroundColor = darkCardBackgroundColor,
    textColorFirst = darkTextFirstColor,
    textColorSecond = darkTextSecondColor,
    textColorThird = darkTextThirdColor,
    textColorOnPrimary = darkTextColorOnPrimary,
    appBackground = darkAppBackground,
    cardBorderColor = darkCardBorderColor,
    doneColor = darkDoneColor,
    errorColor = errorColor
)

private val LocalColorsProvider = staticCompositionLocalOf {
    CustomLightColors
}

@Composable
private fun CustomLocalProvider(
    colors: CustomColors,
    content: @Composable () -> Unit
){
    val colorPalette = remember {
        colors.copy()
    }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.primaryColor,
        backgroundColor = colors.primaryColor.copy(alpha = 0.4f),
    )

    colorPalette.update(colors)
    CompositionLocalProvider(
        LocalColorsProvider provides colorPalette,
        LocalTextSelectionColors provides customTextSelectionColors,
        content = content
    )
}

private val CustomTheme.colors: Pair<Colors, CustomColors>

    get() = when(this){
        CustomTheme.LIGHT -> lightColors() to CustomLightColors
        CustomTheme.DARK -> darkColors() to CustomDarkColors
    }

object CustomThemeManager{
    val colors: CustomColors
        @Composable
        get() = LocalColorsProvider.current

    var customTheme by mutableStateOf(CustomTheme.LIGHT)

    fun isSystemDarkTheme(): Boolean{
        return customTheme == CustomTheme.DARK
    }
}

@Composable
fun CustomJetpackComposeTheme(
    customTheme: CustomTheme = CustomThemeManager.customTheme,
    content: @Composable () -> Unit
){
    val (colorPalette, lcColor) = customTheme.colors

    CustomLocalProvider(colors = lcColor) {
        MaterialTheme(
            colors = colorPalette.copy(primary = lcColor.primaryColor, secondary = lcColor.primaryColor),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}