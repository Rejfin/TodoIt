package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import dev.rejfin.todoit.ui.components.SettingEntry
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.viewmodels.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.BuildConfig
import dev.rejfin.todoit.R
import dev.rejfin.todoit.ui.dialogs.DarkModePickerDialog
import dev.rejfin.todoit.ui.dialogs.DelaySetterDialog

@Destination
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()){
    val uiState = viewModel.uiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomThemeManager.colors.appBackground)
    ) {
        Text(
            stringResource(id = R.string.app_settings),
            fontSize = 16.sp,
            color = CustomThemeManager.colors.primaryColor,
            modifier = Modifier
                .padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
        )
        SettingEntry(
            title = stringResource(id = R.string.dark_mode),
            desc = uiState.darkModeDescription,
            icon = Icons.Default.ModeNight,
            modifier = Modifier.clickable {
                uiState.showOptionsPickerDialog = true
            }
        )
        Spacer(modifier = Modifier
            .height(2.dp)
            .background(CustomThemeManager.colors.textColorSecond))
        SettingEntry(
            title = stringResource(id = R.string.notification_reminder),
            desc = stringResource(id = R.string.notification_settings_desc),
            icon = Icons.Default.Schedule,
            modifier = Modifier.clickable {
                uiState.showNotificationReminderTimeDialog = true
            }
        )
        Spacer(modifier = Modifier
            .height(2.dp)
            .background(CustomThemeManager.colors.textColorSecond))

        Text(
            stringResource(id = R.string.about),
            fontSize = 16.sp,
            color = CustomThemeManager.colors.primaryColor,
            modifier = Modifier
                .padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
        )
        SettingEntry(
            title = stringResource(id = R.string.version),
            desc = "TodoIt v${BuildConfig.VERSION_NAME}",
            icon = Icons.Default.Info,
        )
        SettingEntry(
            title = stringResource(id = R.string.send_feedback),
            desc = stringResource(id = R.string.feedback_settings_desc),
            icon = Icons.Default.Email,
            modifier = Modifier.clickable {
                viewModel.sendFeedback()
            }
        )

        if(uiState.showOptionsPickerDialog){
            DarkModePickerDialog(initSelectedMode = uiState.darkMode, onConfirm = {mode, desc ->
                viewModel.changeDarkMode(mode, desc)
            })
        }

        if(uiState.showNotificationReminderTimeDialog){
            DelaySetterDialog(
                initTime = uiState.notificationReminderTimeInMinutes.toString(),
                onDialogClose = {
                    viewModel.changeNotificationTime(null)
                },
                onTimeSet = {
                    viewModel.changeNotificationTime(it)
                }
            )
        }
    }
}

@Composable
@Preview
fun SettingsScreenPreview(){
    SettingsScreen()
}