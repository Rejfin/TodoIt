package dev.rejfin.todoit.viewmodels

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import dev.rejfin.todoit.BuildConfig
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.states.DarkModeSettings
import dev.rejfin.todoit.models.states.SettingsUiState
import dev.rejfin.todoit.ui.theme.CustomTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private var _uiState by mutableStateOf(SettingsUiState())
    val uiState: SettingsUiState
        get() = _uiState

    init {
        val pref = getApplication<Application>().getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
        if(pref.contains("dark_mode")) {
            _uiState = if (pref.getString("dark_mode", "") == "Light") {
                _uiState.copy(darkMode = DarkModeSettings.LIGHT, darkModeDescription = application.getString(R.string.light_mode))
            } else if (pref.getString("dark_mode", "") == "Dark") {
                _uiState.copy(darkMode = DarkModeSettings.DARK, darkModeDescription = application.getString(R.string.dark_mode))
            }else{
                _uiState.copy(darkMode = DarkModeSettings.SYSTEM, darkModeDescription = application.getString(R.string.use_system_settings))
            }
        }
        if(pref.contains("notification_time")){
            _uiState = _uiState.copy(notificationReminderTimeInMinutes = pref.getInt("notification_time", 15))
        }
    }

    fun showDarkModeDialog(){
        _uiState = _uiState.copy(showOptionsPickerDialog = true)
    }

    fun showNotificationReminderTimeDialog(){
        _uiState = _uiState.copy(showNotificationReminderTimeDialog = true)
    }

    fun changeDarkMode(mode: DarkModeSettings, desc: String){
        if(mode != _uiState.darkMode){
            val pref = getApplication<Application>().getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
            when(mode){
                DarkModeSettings.DARK -> {
                    pref.edit().putString("dark_mode", "Dark").commit()
                    CustomThemeManager.customTheme = CustomTheme.DARK
                }
                DarkModeSettings.LIGHT -> {
                    pref.edit().putString("dark_mode", "Light").commit()
                    CustomThemeManager.customTheme = CustomTheme.LIGHT
                }
                DarkModeSettings.SYSTEM -> {
                    pref.edit().putString("dark_mode", "System").commit()

                    when (getApplication<Application>().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> CustomThemeManager.customTheme = CustomTheme.DARK
                        Configuration.UI_MODE_NIGHT_NO -> CustomThemeManager.customTheme = CustomTheme.LIGHT
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> CustomThemeManager.customTheme = CustomTheme.LIGHT
                    }
                }
            }
        }
        _uiState = _uiState.copy(showOptionsPickerDialog = false, darkMode = mode, darkModeDescription = desc)
    }

    fun changeNotificationTime(time:Int?){
        _uiState = if(time != null){
            val pref = getApplication<Application>().getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
            pref.edit().putInt("notification_time", time).commit()
            _uiState.copy(showNotificationReminderTimeDialog = false, notificationReminderTimeInMinutes = time)
        }else{
            _uiState.copy(showNotificationReminderTimeDialog = false)
        }
    }

    fun sendFeedback(){
        val intentEmail = Intent(Intent.ACTION_SENDTO)
        intentEmail.data = Uri.parse("mailto:")
        intentEmail.putExtra(Intent.EXTRA_EMAIL, Array(1) { "arakdiusz.palka@outlook.com" })
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback - TodoIt v${BuildConfig.VERSION_NAME}")
        val newIntent = Intent.createChooser(intentEmail, "Send feedback")
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            getApplication<Application>().applicationContext.startActivity(newIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(getApplication(),
                "activity for sending email its not installed on device", Toast.LENGTH_LONG).show()
        }
    }
}