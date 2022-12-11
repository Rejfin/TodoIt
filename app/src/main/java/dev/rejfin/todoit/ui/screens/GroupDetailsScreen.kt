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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.viewmodels.GroupDetailViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.ui.components.Calendar
import dev.rejfin.todoit.ui.components.CustomImage
import dev.rejfin.todoit.ui.components.TaskCard
import dev.rejfin.todoit.ui.dialogs.*
import dev.rejfin.todoit.ui.screens.destinations.HomeScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.NewTaskScreenDestination
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Destination
@Composable
fun GroupDetailsScreen(
    navigator: DestinationsNavigator?,
    resultRecipient: ResultRecipient<NewTaskScreenDestination, TaskModel>?,
    groupId: String,
    viewModel: GroupDetailViewModel = viewModel()
) {
    resultRecipient?.onNavResult { result ->
        if(result is NavResult.Value){
            viewModel.uiState.taskToShowDetails = result.value
            viewModel.switchTaskListDay(result.value.startDate)
        }
    }

    val uiState = viewModel.uiState
    val mContext = LocalContext.current

    var confirmationDeleteDialog by remember { mutableStateOf(false) }
    var taskToRemove by remember { mutableStateOf<TaskModel?>(null) }

    LaunchedEffect(key1 = Unit) {
        viewModel.setGroupId(groupId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(CustomThemeManager.colors.appBackground)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CustomThemeManager.colors.cardBackgroundColor)
                    .clickable {
                        viewModel.showGroupDetails()
                    }
            ) {
                CustomImage(
                    imageUrl = uiState.groupData.imageUrl,
                    contentDescription = uiState.groupData.name,
                    size = DpSize(50.dp, 50.dp),
                    placeholder = rememberVectorPainter(Icons.Filled.Group),
                    backgroundColor = CustomThemeManager.colors.appBackground,
                    imageResize = 12.dp,
                    modifier = Modifier.padding(8.dp)
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = uiState.groupData.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        color = CustomThemeManager.colors.textColorFirst,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                    ) {
                        viewModel.memList.onEachIndexed { index, member ->
                            if(index < 5){
                                CustomImage(
                                    imageUrl = member.value.imageUrl,
                                    contentDescription = member.value.displayName,
                                    size = DpSize(20.dp, 20.dp),
                                    placeholder = rememberVectorPainter(Icons.Filled.Person),
                                    backgroundColor = CustomThemeManager.colors.appBackground,
                                    imageResize = 1.dp,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }else{
                                Text("+${uiState.groupData.membersList.size - 5}")
                            }
                        }

                    }
                }
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
                            .align(Alignment.CenterHorizontally)
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
                        TaskCard(task = task,
                            userId = uiState.userId,
                            showRemoveButton = (task.ownerId == uiState.userId || uiState.groupData.ownerId == uiState.userId) && !task.done && task.endTimestamp > viewModel.calendarUtility.getCurrentTimestamp(),
                            modifier = Modifier.clickable {
                                viewModel.showTaskDetails(task)
                            }, onRemoveClick = {
                                taskToRemove = it
                                confirmationDeleteDialog = true
                            }, onBellClick = {
                                viewModel.setNotification(mContext, it, uiState.groupData.name)
                            })
                    }
                }
            }

            if (confirmationDeleteDialog) {
                CustomDialog(
                    dialogType = DialogType.DECISION,
                    title = stringResource(id = R.string.confirm_task_remove_title),
                    message = stringResource(id = R.string.task_remove_text, taskToRemove!!.title),
                    onCancelClick = {
                        confirmationDeleteDialog = false
                        taskToRemove = null
                    },
                    onConfirmClick = {
                        viewModel.removeTask(taskToRemove!!)
                        confirmationDeleteDialog = false
                        taskToRemove = null
                    }
                )
            }
            if (uiState.errorMessage != null) {
                CustomDialog(
                    dialogType = DialogType.ERROR,
                    message = uiState.errorMessage!!,
                    onConfirmClick = {
                        viewModel.clearErrorMessages()
                    }
                )
            }
            if (uiState.infoMessage != null) {
                CustomDialog(
                    dialogType = DialogType.INFO,
                    message = uiState.infoMessage!!,
                    onConfirmClick = {
                        viewModel.clearInfoMessages()
                    }
                )
            }
            if (uiState.showDetailsDialog) {
                TaskDetailsDialog(
                    task = uiState.taskToShowDetails!!,
                    onClose = {
                        viewModel.hideTaskDetails()
                    },
                    onSave = {
                        viewModel.taskPartsUpdate(it)
                        viewModel.hideTaskDetails()
                    },
                    onMarkAsDone = {
                        viewModel.markTaskAsDone(it)
                        viewModel.hideTaskDetails()
                    },
                    onEditClick = {
                        navigator?.navigate(NewTaskScreenDestination(groupId, it))
                    },
                    userId = uiState.userId,
                    currentTimestamp = viewModel.calendarUtility.getCurrentTimestamp()
                )
            }
        }

        FloatingActionButton(
            onClick = {
                navigator?.navigate(NewTaskScreenDestination(groupId))
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
    if (uiState.showGroupDetails) {
        EditGroupDialog(
            uiState.groupData,
            viewModel.memList,
            uiState.userId,
            onSaveClick = { name, description, imageUri ->
                viewModel.updateGroupInfo(name, description, imageUri, uiState.groupData)
            },
            sendRequestToUser = {
                viewModel.sendInvitation(it)
            },
            onCloseClick = { viewModel.closeGroupDetails() },
            onUserLeaveGroup = {
                viewModel.removeUserFromGroup(it)
            }
        )
    }
    if(uiState.endedGroupRemovingUser && uiState.userRemovedFromGroup != null){
        if(uiState.userId == uiState.userRemovedFromGroup!!.id || uiState.groupData.membersList.isEmpty()){
            navigator?.navigate(HomeScreenDestination()){
                popUpTo(NavGraphs.root) {
                    inclusive = true
                }
            }
        }else{
            uiState.userRemovedFromGroup = null
            uiState.endedGroupRemovingUser = false
        }
    }
}