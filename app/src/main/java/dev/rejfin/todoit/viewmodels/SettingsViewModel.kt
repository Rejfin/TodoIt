package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dev.rejfin.todoit.models.states.DarkModeSettings
import dev.rejfin.todoit.models.states.SettingsUiState

class SettingsViewModel : ViewModel() {
    private var _uiState by mutableStateOf(SettingsUiState())
    val uiState: SettingsUiState
        get() = _uiState


    fun showDarkModeDialog(){
        _uiState = _uiState.copy(showOptionsPickerDialog = true)
    }

    fun changeDarkMode(mode: DarkModeSettings, desc: String){
        if(mode != _uiState.darkMode){
            //TODO change dark mode
        }
        _uiState = _uiState.copy(showOptionsPickerDialog = false, darkMode = mode, darkModeDescription = desc)
        println(uiState)
    }
}