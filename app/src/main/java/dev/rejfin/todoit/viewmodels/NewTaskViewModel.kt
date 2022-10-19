package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.rejfin.todoit.models.TaskUiState

class NewTaskViewModel: ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())
        private set
}