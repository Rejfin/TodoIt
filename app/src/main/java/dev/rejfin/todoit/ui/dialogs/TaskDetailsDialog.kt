package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.TaskPartModel
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun TaskDetailsDialog(task: TaskModel,
                      userId: String,
                      currentTimestamp: Long,
                      modifier: Modifier = Modifier,
                      onClose: () -> Unit = {},
                      onEditClick: (TaskModel) -> Unit = {},
                      onMarkAsDone: (TaskModel) -> Unit = {},
                      onSave: (TaskModel) -> Unit = {}
){
    var editedModel by remember { mutableStateOf(task) }
    var editedData by remember { mutableStateOf(false) }

    fun updateTaskPart(index:Int){
        val taskList = editedModel.taskParts.toMutableList()
        taskList[index] = taskList[index].copy(status = !taskList[index].status)
        editedModel = editedModel.copy(taskParts = taskList)
        editedData = task.taskParts != editedModel.taskParts
    }

    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
        ),
        onDismissRequest = {
            onClose()
        },
        title = {
        },
        text = {
            Column(modifier = modifier
                .fillMaxWidth()
                .background(CustomThemeManager.colors.cardBackgroundColor)
            ) {

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(text = "Task Details")

                    if(!task.done && task.ownerId == userId && task.endTimestamp > currentTimestamp && task.lockedByUserId == null){
                        IconButton(onClick = {
                            onEditClick(task)
                        }) {
                            Icon(Icons.Default.Edit, stringResource(id = R.string.edit_task))
                        }
                    }
                    
                    if(task.lockedByUserId != null && task.lockedByUserId != userId){
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "task locked")
                    }
                }


                Text(
                    text = stringResource(id = R.string.task_title),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = task.title,
                    color = CustomThemeManager.colors.textColorFirst,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = stringResource(id = R.string.task_description),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = task.description,
                    color = CustomThemeManager.colors.textColorFirst,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = stringResource(id = R.string.reward_for_task),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = "- ${task.xpForTask}xp",
                    color = CustomThemeManager.colors.textColorFirst,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = stringResource(id = R.string.day_of_completion),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = String.format("%02d.%02d.%d",task.startDate.day, task.startDate.month, task.startDate.year),
                    color = CustomThemeManager.colors.textColorFirst,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                if(!task.allDay){
                    Row() {
                        Text(
                            text = String.format("%02d:%02d - ",task.startDate.hour, task.startDate.minutes),
                            color = CustomThemeManager.colors.textColorFirst,
                            fontSize = 11.sp
                        )
                        Text(
                            text = String.format("%02d:%02d",task.endDate.hour, task.endDate.minutes),
                            color = CustomThemeManager.colors.textColorFirst,
                            fontSize = 11.sp
                        )
                    }
                }

                if(task.taskParts.isNotEmpty()){
                    Text(
                        text = stringResource(id = R.string.task_parts),
                        color = CustomThemeManager.colors.textColorSecond,
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    )
                }

                editedModel.taskParts.forEachIndexed {index, it->
                    Row(
                        verticalAlignment = CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clickable {
                                if (!task.done && (task.lockedByUserId == null || task.lockedByUserId == userId)) {
                                    updateTaskPart(index)
                                }
                            }
                    ) {
                        if(it.status){
                            Icon(imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "task done",
                                modifier = Modifier.size(15.dp),
                                tint = if(task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.doneColor
                            )
                        }else{
                            Icon(imageVector = Icons.Outlined.Circle,
                                contentDescription = "task undone",
                                modifier = Modifier.size(15.dp),
                                tint = CustomThemeManager.colors.primaryColor
                            )
                        }
                        Text(text = it.desc,
                            fontSize = 18.sp,
                            color = if(it.status) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.textColorFirst,
                            style = if(it.status) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if(task.taskParts.isEmpty() && !task.done && task.endTimestamp > currentTimestamp){
                    Button(onClick = {
                        onMarkAsDone(task)
                    }, modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)) {
                        Text(text = stringResource(id = R.string.mark_as_done))
                    }
                }
                Button(onClick = {
                    if(editedData){
                        onSave(editedModel)
                    }else{
                        onClose()
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text(text = if(editedData) stringResource(id = R.string.save) else stringResource(id = R.string.ok))
                }
            }
        }
    )


}

@Preview
@Composable
fun TaskDetails_Preview(){
    CustomJetpackComposeTheme {
        TaskDetailsDialog(task = TaskModel(
            id = "asdasdasd",
            title = "asdasdasd",
            description = "asdhgfhds bhbf sdhbf jsdhbf sdhbf",
            taskParts = listOf(
                TaskPartModel(false, "tesfdf"),
                TaskPartModel(true, "asdasdasd"),
                TaskPartModel(true, " asdjhfgb sd ")
            ),
            xpForTask = 25,
            false,
            startDate = CustomDateFormat(2022, 12 , 12, 12, 33),
            endDate = CustomDateFormat(2022, 12 ,12, 15, 33),
            endTimestamp = 1678419104L,
            lockedByUserId = null,
            done = false,
            ownerId = "asdasd",
        ), "asdasd", 1668419104L)
    }
}