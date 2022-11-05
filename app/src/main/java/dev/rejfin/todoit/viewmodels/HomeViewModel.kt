package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import dev.rejfin.todoit.models.states.BaseTaskUiState
import dev.rejfin.todoit.models.states.HomeUiState
import kotlinx.coroutines.launch

class HomeViewModel : BaseTaskManagerViewModel() {
    private val _uiState by mutableStateOf(HomeUiState())
    val uiState: HomeUiState
        get() = _uiState

    override fun getBaseUiState(): BaseTaskUiState {
        return _uiState
    }

    init {
        _uiState.loggedUserDisplayName = auth.currentUser!!.displayName!!

        viewModelScope.launch {
            getInitialData()
        }
    }
}