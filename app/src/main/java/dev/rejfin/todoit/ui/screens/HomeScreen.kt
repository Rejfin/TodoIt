package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.viewmodels.HomeViewModel
import dev.rejfin.todoit.ui.components.Calendar
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.states.HomeUiState
import dev.rejfin.todoit.ui.components.TaskCard
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.ui.dialogs.ErrorDialog
import dev.rejfin.todoit.ui.dialogs.InfoDialog
import dev.rejfin.todoit.ui.dialogs.TaskDetailsDialog
import dev.rejfin.todoit.ui.screens.destinations.NewTaskScreenDestination

@Destination
@Composable
fun HomeScreen(navigator: DestinationsNavigator?, viewModel: HomeViewModel = viewModel()) {
    val uiState: HomeUiState = viewModel.uiState

    var confirmationDeleteDialog by remember { mutableStateOf(false) }
    var taskToRemove by remember { mutableStateOf<TaskModel?>(null) }

    val mContext = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(CustomThemeManager.colors.appBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CustomThemeManager.colors.cardBackgroundColor)
            ) {
                Text(
                    text = stringResource(id = R.string.welcome, uiState.loggedUserDisplayName),
                    fontSize = 18.sp,
                    color = CustomThemeManager.colors.textColorFirst,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp)
                )
                Text(
                    text = if(uiState.numberOfAllTasks > 0)
                        stringResource(id = R.string.done_tasks,
                            uiState.numberOfDoneTask,
                            uiState.numberOfAllTasks
                        )
                    else
                        stringResource(id = R.string.dont_have_plans),
                    color = CustomThemeManager.colors.textColorSecond,
                    modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
                )
            }
            Calendar(
                uiState.calendarDays,
                onDayClick = {
                    viewModel.switchTaskListDay(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomThemeManager.colors.cardBackgroundColor)
                    .padding(horizontal = 8.dp, vertical = 14.dp)
            )
            
            if(uiState.selectedTaskList.isEmpty()){
                if(uiState.isDataLoading){
                    LinearProgressIndicator(modifier = Modifier.padding(50.dp))
                }else{
                    Text(
                        text = stringResource(id = R.string.none_task_today),
                        color = CustomThemeManager.colors.textColorSecond,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(CenterHorizontally)
                            .padding(50.dp)
                    )
                }
            }else{
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(items = uiState.selectedTaskList,
                        key = { task -> task.id },
                        contentType = { TaskModel::class.java }
                    )
                    { task ->
                        TaskCard(
                            task = task,
                            userId = uiState.userId,
                            modifier = Modifier.clickable {
                                viewModel.showTaskDetails(task)
                            }, onRemoveClick = {
                                taskToRemove = it
                                confirmationDeleteDialog = true
                            },
                            showRemoveButton = !task.done && task.endTimestamp > viewModel.calendarUtility.getCurrentTimestamp(),
                            onBellClick = {
                                viewModel.setNotification(mContext, task)
                            }
                        )
                    }
                }
            }
            
            if (confirmationDeleteDialog) {
                InfoDialog(
                    title = stringResource(id = R.string.confirm_task_remove_title),
                    infoText = stringResource(id = R.string.task_remove_text, taskToRemove!!.title),
                    isDecisionDialog = true,
                    onConfirm = {
                        viewModel.removeTask(taskToRemove!!)
                        confirmationDeleteDialog = false
                        taskToRemove = null
                    },
                    onCancel = {
                        confirmationDeleteDialog = false
                        taskToRemove = null
                    }
                )
            }
            if (uiState.errorMessage != null) {
                ErrorDialog(
                    title = stringResource(id = R.string.error),
                    errorText = uiState.errorMessage!!
                ) {
                    viewModel.clearErrorMessages()
                }
            }
            if (uiState.showDetailsDialog) {
                TaskDetailsDialog(
                    task = uiState.taskToShowDetails!!,
                    onClose = {
                        viewModel.hideTaskDetails()
                    },
                    onEditClick = {
                        navigator?.navigate(NewTaskScreenDestination(null, it))
                    },
                    onMarkAsDone = {
                        viewModel.markTaskAsDone(it)
                        viewModel.hideTaskDetails()
                    },
                    onSave = {
                        viewModel.taskPartsUpdate(it)
                        viewModel.hideTaskDetails()
                    },
                    userId = uiState.userId,
                    currentTimestamp = viewModel.calendarUtility.getCurrentTimestamp()
                )
            }
        }

        FloatingActionButton(
            onClick = {
                navigator?.navigate(NewTaskScreenDestination(null))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            backgroundColor = CustomThemeManager.colors.secondaryColor
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.new_task),
                tint = CustomThemeManager.colors.textColorOnPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navigator = null)
}