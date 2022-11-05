package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import kotlin.math.roundToInt

@Composable
fun StepsProgressBar(modifier: Modifier = Modifier, numberOfSteps: Int, currentStep: Int, onSelectionChanged: (step: Int)-> Unit) {
    //var selection by remember{ mutableStateOf(currentStep) }

    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxWidth()) {
        Slider(
            modifier = Modifier.fillMaxWidth(),
            onValueChange = {
                //currentStep = it.roundToInt()
                onSelectionChanged(it.roundToInt())
            },
            value = currentStep*1f,
            colors = SliderDefaults.colors(
                inactiveTrackColor = CustomThemeManager.colors.textColorThird,
                activeTrackColor = CustomThemeManager.colors.primaryColor,
                thumbColor = CustomThemeManager.colors.primaryColor,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent,
            ),
            steps = 5,
            valueRange = 1f..5f,
        )
        Steps(numberOfSteps, currentStep)
    }
}

@Composable
fun Steps(numberOfSteps: Int, selected: Int) {
    val primaryColor = CustomThemeManager.colors.primaryColor
    val emptyColor = CustomThemeManager.colors.cardBackgroundColor
    val secondColor = CustomThemeManager.colors.textColorThird
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        val drawPadding: Float = with(LocalDensity.current) { 10.dp.toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val distance: Float = (size.width.minus(2 * drawPadding)).div(numberOfSteps.minus(1))
            (0..numberOfSteps).forEach { step ->
                drawCircle(color = primaryColor, radius = 25f, center = Offset(drawPadding + step.times(distance), size.height/2))
                drawCircle(color = emptyColor, radius = 18f, center = Offset(drawPadding + step.times(distance), size.height/2))
                drawCircle(color = if (selected - 1 >= step) primaryColor else secondColor, radius = 13f, center = Offset(drawPadding + step.times(distance), size.height/2))
            }
        }
    }
}

@Preview
@Composable
fun StepsProgressBarPreview() {
    val currentStep = remember { mutableStateOf(4) }
    StepsProgressBar(modifier = Modifier.fillMaxWidth(), numberOfSteps = 5, currentStep = currentStep.value, {})
}