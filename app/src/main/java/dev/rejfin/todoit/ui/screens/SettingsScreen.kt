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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.ui.components.SettingEntry
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.viewmodels.SettingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.ui.dialogs.DarkModePickerDialog

@Destination
@Composable
fun SettingsScreen(navigator: DestinationsNavigator?, viewModel: SettingsViewModel = viewModel()){
    val uiState = viewModel.uiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomThemeManager.colors.appBackground)
    ) {
        Text("App Settings",
            fontSize = 16.sp,
            color = CustomThemeManager.colors.primaryColor,
            modifier = Modifier
                .padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
        )
        SettingEntry(
            title = "Dark mode",
            desc = uiState.darkModeDescription,
            icon = Icons.Default.ModeNight,
            modifier = Modifier.clickable {
                viewModel.showDarkModeDialog()
            }
        )
        Spacer(modifier = Modifier
            .height(2.dp)
            .background(CustomThemeManager.colors.textColorSecond))
        SettingEntry(
            title = "Notification reminder",
            desc = "Set how much before the start of the task you want the notification to be displayed",
            icon = Icons.Default.Schedule,
        )
        Spacer(modifier = Modifier
            .height(2.dp)
            .background(CustomThemeManager.colors.textColorSecond))

        Text("About",
            fontSize = 16.sp,
            color = CustomThemeManager.colors.primaryColor,
            modifier = Modifier
                .padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
        )
        SettingEntry(
            title = "Version",
            desc = "TodoIt v0.1",
            icon = Icons.Default.Info,
        )
        SettingEntry(
            title = "Send feedback",
            desc = "Report technical issues or suggest new feature",
            icon = Icons.Default.Email,
        )

        if(uiState.showOptionsPickerDialog){
            DarkModePickerDialog(initSelectedMode = uiState.darkMode, onConfirm = {mode, desc ->
                viewModel.changeDarkMode(mode, desc)
            })
        }
    }
}

@Composable
@Preview
fun SettingsScreenPreview(){
    SettingsScreen(navigator = null)
}