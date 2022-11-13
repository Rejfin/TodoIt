package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.states.DarkModeSettings
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun DarkModePickerDialog(initSelectedMode: DarkModeSettings,
               onConfirm: (DarkModeSettings, String) -> Unit = {_,_->}){

    val radioOptionsToDisplay = listOf(stringResource(id = R.string.use_system_settings), stringResource(id = R.string.dark_mode), stringResource(id = R.string.light_mode))
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptionsToDisplay[0] ) }
    var selectedMode by remember{ mutableStateOf(initSelectedMode)}

    fun computeMode(index: Int){
        selectedMode =  when (index) {
            0 -> {
                DarkModeSettings.SYSTEM
            }
            1 -> {
                DarkModeSettings.DARK
            }
            else -> {
                DarkModeSettings.LIGHT
            }
        }
    }

    LaunchedEffect(key1 = Unit){
        when (initSelectedMode) {
            DarkModeSettings.SYSTEM -> {
                onOptionSelected(radioOptionsToDisplay[0])
            }
            DarkModeSettings.DARK -> {
                onOptionSelected(radioOptionsToDisplay[1])
            }
            else -> {
                onOptionSelected(radioOptionsToDisplay[2])
            }
        }
    }

    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
        backgroundColor = CustomThemeManager.colors.cardBackgroundColor,
        onDismissRequest = {
        },
        title = {
            Text(text = stringResource(id = R.string.choose_dark_mode), modifier = Modifier.fillMaxWidth())
        },
        text = {
            Column {
                radioOptionsToDisplay.forEachIndexed{ index, text ->
                    Row(
                        verticalAlignment = CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = {
                                    onOptionSelected(text)
                                    computeMode(index)
                                }
                            )
                            .padding(horizontal = 16.dp)
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) }
                        )
                        Text(
                            text = text,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    onClick = {
                        onConfirm(selectedMode, selectedOption)
                    }
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun DarkModePickerPreview(){
    DarkModePickerDialog(initSelectedMode = DarkModeSettings.SYSTEM)
}