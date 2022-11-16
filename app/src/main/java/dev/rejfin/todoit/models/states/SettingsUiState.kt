package dev.rejfin.todoit.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SettingsUiState {
    var darkMode by mutableStateOf(DarkModeSettings.SYSTEM)
    var darkModeDescription by mutableStateOf("Use system settings")
    var notificationReminderTimeInMinutes by mutableStateOf(15)
    var showOptionsPickerDialog by mutableStateOf(false)
    var showNotificationReminderTimeDialog by mutableStateOf(false)
}

enum class DarkModeSettings{
    DARK, LIGHT, SYSTEM
}
