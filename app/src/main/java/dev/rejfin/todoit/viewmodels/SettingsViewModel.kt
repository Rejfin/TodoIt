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
import androidx.lifecycle.AndroidViewModel
import dev.rejfin.todoit.BuildConfig
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.states.DarkModeSettings
import dev.rejfin.todoit.models.states.SettingsUiState
import dev.rejfin.todoit.ui.theme.CustomTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val pref = application.getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
    private val prefEditor = pref.edit()
    private var _uiState by mutableStateOf(SettingsUiState())
    val uiState: SettingsUiState
        get() = _uiState

    /**
     * get default dark mode settings and notification time
     * then set it in uiState to show right value for user
     */
    init {
        if(pref.contains("dark_mode")) {
            if (pref.getString("dark_mode", "") == "Light") {
                _uiState.apply {
                    darkMode = DarkModeSettings.LIGHT
                    darkModeDescription = application.getString(R.string.light_mode)
                }
            } else if (pref.getString("dark_mode", "") == "Dark") {
                _uiState.apply {
                    darkMode = DarkModeSettings.DARK
                    darkModeDescription = application.getString(R.string.dark_mode)
                }
            }else{
                _uiState.apply {
                    darkMode = DarkModeSettings.SYSTEM
                    darkModeDescription = application.getString(R.string.use_system_settings)
                }
            }
        }
        if(pref.contains("notification_time")){
            _uiState.notificationReminderTimeInMinutes = pref.getInt("notification_time", 15)
        }
    }

    /**
     * function will be called to change the appearance of the application
     * based on user selection application will change its appearance
     * will only be triggered if the setting has been changed to another
     */
    fun changeDarkMode(mode: DarkModeSettings, desc: String){
        if(mode != _uiState.darkMode){
            _uiState.apply {
                darkMode = mode
                darkModeDescription = desc
            }
            when(mode){
                DarkModeSettings.DARK -> {
                    prefEditor.putString("dark_mode", "Dark").commit()
                    CustomThemeManager.customTheme = CustomTheme.DARK
                }
                DarkModeSettings.LIGHT -> {
                    prefEditor.putString("dark_mode", "Light").commit()
                    CustomThemeManager.customTheme = CustomTheme.LIGHT
                }
                DarkModeSettings.SYSTEM -> {
                    prefEditor.putString("dark_mode", "System").commit()

                    /** get system config and read ui settings then set appropriate theme */
                    val config = getApplication<Application>().resources.configuration
                    when (config.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            CustomThemeManager.customTheme = CustomTheme.DARK
                        }
                        Configuration.UI_MODE_NIGHT_NO -> {
                            CustomThemeManager.customTheme = CustomTheme.LIGHT
                        }
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                            CustomThemeManager.customTheme = CustomTheme.LIGHT
                        }
                    }
                }
            }
        }
        _uiState.showOptionsPickerDialog = false
    }

    /**
     * change amount of minutes before notification should appear
     * this settings will be used only if the task has strict time window
     */
    fun changeNotificationTime(time:Int?){
        if(time != null){
            pref.edit().putInt("notification_time", time).apply()
            _uiState.apply {
                showNotificationReminderTimeDialog = false
                notificationReminderTimeInMinutes = time
            }
        }else{
            _uiState.showNotificationReminderTimeDialog = false
        }
    }

    /**
     * function is responsible to move user to an application that supports sending emails
     * when the user selects the application for emails, the title and recipient of the message
     * will be set and the user will only fill in the message body
     */
    fun sendFeedback(){
        val intentEmail = Intent(Intent.ACTION_SENDTO)
        intentEmail.data = Uri.parse("mailto:")
        intentEmail.putExtra(Intent.EXTRA_EMAIL, Array(1) { "arakdiusz.palka@outlook.com" })
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback - TodoIt v${BuildConfig.VERSION_NAME}")
        val newIntent = Intent.createChooser(intentEmail, "Send feedback")
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            getApplication<Application>().applicationContext.startActivity(newIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                getApplication(),
                "Application for sending email its not installed on device",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}