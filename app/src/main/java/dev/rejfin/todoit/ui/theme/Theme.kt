package dev.rejfin.todoit.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*


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
    doneColor = doneColor
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
    doneColor = darkDoneColor
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

    colorPalette.update(colors)
    CompositionLocalProvider(LocalColorsProvider provides colorPalette, content = content)
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
            colors = colorPalette,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}