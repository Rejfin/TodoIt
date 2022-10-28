package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.viewmodels.GroupDetailViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.ui.components.Calendar
import dev.rejfin.todoit.ui.components.TaskCard
import dev.rejfin.todoit.ui.dialogs.ErrorDialog
import dev.rejfin.todoit.ui.screens.destinations.NewTaskScreenDestination
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Destination
@Composable
fun GroupDetailsScreen(navigator: DestinationsNavigator?, groupId: String, viewModel: GroupDetailViewModel = viewModel()){
    val uiState = viewModel.uiState

    LaunchedEffect(key1 = Unit){
        viewModel.setGroupId(groupId)
    }

    Box(modifier = Modifier.fillMaxSize()){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(CustomThemeManager.colors.appBackground)) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
            ) {
                Text(text = uiState.groupName,
                    fontSize = 18.sp,
                    color = CustomThemeManager.colors.textColorFirst,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp)
                )
                Text(text = stringResource(id = R.string.done_tasks, uiState.numberOfDoneTask, uiState.numberOfAllTasks) ,
                    color= CustomThemeManager.colors.textColorSecond,
                    modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
                )
            }
            Calendar(viewModel.calendarDays,
                onDayClick = {
                    viewModel.switchTaskListDay(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomThemeManager.colors.cardBackgroundColor)
                    .padding(horizontal = 8.dp, vertical = 14.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ){
                items(items = viewModel.taskList,
                    key = {task -> task.id },
                    contentType = { TaskModel::class.java }
                )
                {task ->
                    TaskCard(task = task, modifier = Modifier.clickable {
                        //viewModel.showTaskDetails(task)
                    })
                }
            }
            if(uiState.errorMessage != null){
                ErrorDialog(title = stringResource(id = R.string.error), errorText = uiState.errorMessage){
                    //viewModel.clearError()
                }
            }
//            if(uiState.showDetailsDialog){
//                TaskDetailsDialog(
//                    task = uiState.taskToShowDetails!!,
//                    onClose = {
//                        viewModel.hideTaskDetails()
//                    })
//            }
        }

        FloatingActionButton(
            onClick = {
                navigator?.navigate(NewTaskScreenDestination(groupId))
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            backgroundColor = CustomThemeManager.colors.secondaryColor){
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.new_task),
                tint = CustomThemeManager.colors.textColorOnPrimary
            )
        }
    }
}