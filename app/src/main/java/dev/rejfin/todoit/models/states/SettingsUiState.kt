package dev.rejfin.todoit.models.states

data class SettingsUiState(
    val darkMode: DarkModeSettings = DarkModeSettings.SYSTEM,
    val darkModeDescription: String = "Use system settings",
    val notificationReminderTimeInMinutes: Int = 15,
    val showOptionsPickerDialog: Boolean = false,
    val showNotificationReminderTimeDialog: Boolean = false
)

enum class DarkModeSettings{
    DARK, LIGHT, SYSTEM
}
