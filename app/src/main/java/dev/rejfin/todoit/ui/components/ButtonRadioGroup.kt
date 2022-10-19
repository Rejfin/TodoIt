package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun ButtonRadioGroup(options: Map<Int, String>, selected: Int, onSelectChanged: (key: Int) -> Unit, modifier: Modifier = Modifier) {
    var selectedOption by remember {
        mutableStateOf(selected)
    }

    val onSelectionChange = { key: Int ->
        selectedOption = key
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        options.forEach { option ->
            Row(
                modifier = Modifier.padding(8.dp),
            ) {
                Text(
                    text = option.value,
                    textAlign = TextAlign.Center,
                    color = CustomThemeManager.colors.textColorOnPrimary,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .clickable {
                            onSelectionChange(option.key)
                            onSelectChanged(option.key)
                        }
                        .background(
                            if (option.key == selectedOption) {
                                CustomThemeManager.colors.primaryColor
                            } else {
                                CustomThemeManager.colors.textColorThird
                            }
                        )
                        .padding(vertical = 8.dp, horizontal = 24.dp,)
                        .widthIn(min = 24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun ButtonRadioGroup_Preview(){
    CustomJetpackComposeTheme {
        ButtonRadioGroup(mapOf(0 to "yes", 1 to "no"), 0, {})
    }
}