package dev.rejfin.todoit.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class CustomTheme{
    LIGHT, DARK
}

//Light colors
val primaryColor = Color(0xFF2E81FF)
val secondaryColor = Color(0xFFFF792E)
val cardBackgroundColor = Color(0xFFFFFFFF)
val textFirstColor = Color(0xFF0D0D0D)
val textSecondColor = Color(0xFF5F5F5F)
val textThirdColor = Color(0xFFA5A5A5)
val textColorOnPrimary = Color(0xFFFFFFFF)
val appBackground = Color(0xFFF0F0F0)
val cardBorderColor = Color(0xFFCACACA)
val doneColor = Color(0xFF03B14B)
val errorColor = Color(0xFFEB3C39)


//Dark colors
val darkPrimaryColor = Color(0xFF2196F3)
val darkSecondaryColor = Color(0xFFFF792E)
val darkCardBackgroundColor = Color(0xFF3F3F3F)
val darkTextFirstColor = Color(0xFFDFDFDF)
val darkTextSecondColor = Color(0xFF838383)
val darkTextThirdColor = Color(0xFFA7A7A7)
val darkTextColorOnPrimary = Color(0xFFFFFFFF)
val darkAppBackground = Color(0xFF1D1D1D)
val darkCardBorderColor = Color(0xFF2C2C2C)
val darkDoneColor = Color(0xFF03B14B)


@Stable
class CustomColors(
    primaryColor: Color,
    secondaryColor: Color,
    cardBackgroundColor: Color,
    textColorFirst: Color,
    textColorSecond: Color,
    textColorThird: Color,
    textColorOnPrimary: Color,
    appBackground: Color,
    cardBorderColor: Color,
    doneColor: Color,
    errorColor: Color
){
    var primaryColor by mutableStateOf(primaryColor)
        private set

    var secondaryColor by mutableStateOf(secondaryColor)
        private set

    var cardBackgroundColor by mutableStateOf(cardBackgroundColor)
        private set

    var textColorFirst by mutableStateOf(textColorFirst)
        private set

    var textColorSecond by mutableStateOf(textColorSecond)
        private set

    var textColorThird by mutableStateOf(textColorThird)
        private set

    var appBackground by mutableStateOf(appBackground)
        private set

    var cardBorderColor by mutableStateOf(cardBorderColor)
        private set

    var textColorOnPrimary by mutableStateOf(textColorOnPrimary)
        private set

    var doneColor by mutableStateOf(doneColor)
        private set

    var errorColor by mutableStateOf(errorColor)
        private set


    fun update(colors: CustomColors){
        this.primaryColor = colors.primaryColor
        this.secondaryColor = colors.secondaryColor
        this.cardBackgroundColor = colors.cardBackgroundColor
        this.textColorFirst = colors.textColorFirst
        this.textColorSecond = colors.textColorSecond
        this.textColorThird = colors.textColorThird
        this.textColorOnPrimary = colors.textColorOnPrimary
        this.appBackground = colors.appBackground
        this.cardBorderColor = colors.cardBorderColor
        this.doneColor = colors.doneColor
        this.errorColor = colors.errorColor
    }

    fun copy() = CustomColors(
        primaryColor,
        secondaryColor,
        cardBackgroundColor,
        textColorFirst,
        textColorSecond,
        textColorThird,
        textColorOnPrimary,
        appBackground,
        cardBorderColor,
        doneColor,
        errorColor
    )
}