package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.states.DarkModeSettings
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun DarkModePickerDialog(
    initSelectedMode: DarkModeSettings,
    onConfirm: (DarkModeSettings, String) -> Unit,
    onCancelClick: () -> Unit
){

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

    BaseDialog(
        dialogType = DialogType.SETTINGS,
        onDismissRequest = {onCancelClick()},
        iconSize = 45.dp,
        content = {
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

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                ) {
                    TextButton(onClick = {
                        onCancelClick()
                    }, modifier = Modifier
                        .background(
                            CustomThemeManager.colors.errorColor.copy(
                                if (CustomThemeManager.isSystemDarkTheme()) {
                                    0.65f
                                } else {
                                    0.35f
                                }
                            )
                        )
                        .weight(1f)
                    ) {
                        Text(
                            stringResource(id = R.string.cancel),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if(CustomThemeManager.isSystemDarkTheme()){
                                CustomThemeManager.colors.textColorOnPrimary
                            }else{
                                CustomThemeManager.colors.errorColor
                            },
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }

                    TextButton(
                        onClick = {
                            onConfirm(selectedMode, selectedOption)
                        }, modifier = Modifier
                            .background(
                                CustomThemeManager.colors.primaryColor.copy(
                                    if (CustomThemeManager.isSystemDarkTheme()) {
                                        0.65f
                                    } else {
                                        0.35f
                                    }
                                )
                            )
                            .weight(1f)
                    ) {
                        Text(
                            stringResource(id = R.string.ok),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if(CustomThemeManager.isSystemDarkTheme()){
                                CustomThemeManager.colors.textColorOnPrimary
                            }else{
                                CustomThemeManager.colors.primaryColor
                            },
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DarkModePickerPreview(){
    CustomJetpackComposeTheme() {
        DarkModePickerDialog(initSelectedMode = DarkModeSettings.SYSTEM, {_,_->}, {})
    }
}