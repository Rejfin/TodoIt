package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import dev.rejfin.todoit.models.states.BaseTaskUiState
import dev.rejfin.todoit.models.states.HomeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.internal.wait

class HomeViewModel : BaseTaskManagerViewModel() {
    private val _uiState by mutableStateOf(HomeUiState())
    val uiState: HomeUiState
        get() = _uiState

    override fun getBaseUiState(): BaseTaskUiState {
        return _uiState
    }

    init {
        if(uiState.calendarDays.isEmpty()){
            viewModelScope.launch {
                _uiState.isDataLoading = true
                getInitialData{
                    _uiState.isDataLoading = false
                }
            }
        }
        _uiState.loggedUserDisplayName = auth.currentUser!!.displayName!!
    }
}