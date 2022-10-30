package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.viewmodels.GroupDetailViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.ui.components.Calendar
import dev.rejfin.todoit.ui.components.TaskCard
import dev.rejfin.todoit.ui.dialogs.EditGroupDialog
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
            ){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.groupData.imageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = rememberVectorPainter(Icons.Filled.Group),
                    contentDescription = uiState.groupData.name,
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Filled.Group),
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .size(50.dp, 50.dp)
                )
                Column(verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(text = uiState.groupData.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        color = CustomThemeManager.colors.textColorFirst,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)){
                        uiState.groupData.membersList.forEach { member->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(member.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = rememberVectorPainter(Icons.Filled.Person),
                                contentDescription = member.displayName,
                                contentScale = ContentScale.Crop,
                                error = rememberVectorPainter(Icons.Filled.Person),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(CircleShape)
                                    .size(20.dp, 20.dp)
                                    .background(CustomThemeManager.colors.textColorThird)
                            )
                        }

                    }
                }
            }

            Calendar(uiState.calendarDays,
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
                items(items = uiState.selectedTaskList,
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
    if(uiState.showGroupDetails){
        EditGroupDialog(
            uiState.groupData,
            viewModel.getUserId(),
            onCreateClick = { _, _, _->},
            onCancelClick = { /*TODO*/ },
            sendRequestToUser = {
                viewModel.sendInvitation(it)
            },
            onCloseClick = {viewModel.closeGroupDetails()}
        )
    }
}