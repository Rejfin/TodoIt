package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.TaskPartModel
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.utils.CalendarUtility

@Composable
fun TaskDetailsDialog(task: TaskModel,
                      userId: String,
                      currentTimestamp: Long,
                      modifier: Modifier = Modifier,
                      onClose: () -> Unit,
                      onEditClick: (TaskModel) -> Unit,
                      onMarkAsDone: (TaskModel) -> Unit,
                      onSave: (TaskModel) -> Unit
){
    var editedModel by remember { mutableStateOf(task) }
    var editedData by remember { mutableStateOf(false) }
    val calendarUtil by remember { mutableStateOf(CalendarUtility()) }

    fun updateTaskPart(index:Int){
        val taskList = editedModel.taskParts.toMutableList()
        taskList[index] = taskList[index].copy(status = !taskList[index].status)
        editedModel = editedModel.copy(taskParts = taskList)
        editedData = task.taskParts != editedModel.taskParts
    }

    BaseDialog(
        dialogType = DialogType.TASK_DETAILS,
        dismissOnClickOutside = true,
        iconSize = 50.dp,
        onDismissRequest = {
            onClose()
        },
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(CustomThemeManager.colors.cardBackgroundColor)
            ){

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                ){
                    Text(
                        text = stringResource(id = R.string.task_details) +
                                if(task.endTimestamp < System.currentTimeMillis() && !task.done){
                                    " (${stringResource(id = R.string.undone)})"
                                }else if(task.done){
                                    " (${stringResource(id = R.string.done)})"
                                }else if(task.lockedByUserId != null){
                                    " (${stringResource(id = R.string.locked)})"
                                }else{
                                     ""
                                },
                        color = CustomThemeManager.colors.textColorFirst,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                    )

                    if(!task.done && task.ownerId == userId && task.endTimestamp > currentTimestamp && task.lockedByUserId == null){
                        IconButton(onClick = {
                            onEditClick(task)
                        }) {
                            Icon(Icons.Default.Edit, stringResource(id = R.string.edit_task))
                        }
                    }

                    if(task.lockedByUserId != null && task.lockedByUserId != userId && task.endTimestamp > currentTimestamp){
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "task locked")
                    }
                }

                Spacer(modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(CustomThemeManager.colors.textColorSecond.copy(alpha = 0.5f))
                    .clip(CircleShape)
                )

                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(CustomThemeManager.colors.cardBackgroundColor)
                        .verticalScroll(rememberScrollState())
                        .weight(weight = 1f, fill = false)
                ) {
                    Text(
                        text = task.title,
                        color = CustomThemeManager.colors.textColorFirst,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp, top = 6.dp)
                            .fillMaxWidth()
                    )

                    Text(
                        text = task.description,
                        color = CustomThemeManager.colors.textColorSecond,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp, top = 3.dp, bottom = 6.dp)
                            .fillMaxWidth()
                    )

                    if(task.taskParts.isNotEmpty()){
                        Text(
                            text = stringResource(id = R.string.task_parts),
                            color = CustomThemeManager.colors.textColorFirst,
                            textAlign = TextAlign.Start,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier
                                .padding(top = 8.dp, start = 12.dp)
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier
                            .padding(start = 12.dp, end = 32.dp, top = 5.dp, bottom = 3.dp)
                            .fillMaxWidth(0.7f)
                            .height(1.dp)
                            .background(CustomThemeManager.colors.textColorSecond.copy(alpha = 0.5f))
                        )
                    }

                    editedModel.taskParts.forEachIndexed {index, it->
                        Row(
                            verticalAlignment = CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 3.dp, horizontal = 16.dp)
                                .clip(CircleShape)
                                .background(CustomThemeManager.colors.textColorThird.copy(alpha = 0.16f))
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .clickable(task.endTimestamp > System.currentTimeMillis() && !task.done && (task.lockedByUserId == null || task.lockedByUserId == userId)) {
                                    updateTaskPart(index)
                                }
                        ) {
                            if(it.status){
                                Icon(imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = stringResource(id = R.string.task_done),
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .size(15.dp),
                                    tint = if(task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.doneColor
                                )
                            }else{
                                Icon(imageVector = Icons.Outlined.Circle,
                                    contentDescription = stringResource(id = R.string.task_undone),
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .size(15.dp),
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

                    if(editedModel.taskParts.isNotEmpty()){
                        Spacer(modifier = Modifier
                            .padding(start = 6.dp, end = 6.dp, top = 8.dp)
                            .fillMaxWidth()
                            .height(2.dp)
                            .clip(CircleShape)
                            .background(CustomThemeManager.colors.textColorSecond.copy(alpha = 0.5f))
                        )
                    }

                    Row(
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Column(){
                            Text(
                                text = calendarUtil.timestampToDateString(task.startTimestamp),
                                color = CustomThemeManager.colors.textColorFirst,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            if(!task.allDay){
                                Text(
                                    text = "${calendarUtil.timestampToHourString(task.startTimestamp)} - ${calendarUtil.timestampToHourString(task.endTimestamp)}",
                                    color = CustomThemeManager.colors.textColorFirst,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "${task.xpForTask}xp",
                            color = CustomThemeManager.colors.textColorFirst,
                            fontSize = 21.sp,
                            modifier = Modifier
                                .padding(end = 16.dp)

                        )
                    }
                }

                // buttons
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(IntrinsicSize.Max)
                ) {
                    if(task.taskParts.isEmpty() && !task.done && task.endTimestamp > currentTimestamp){
                        TextButton(onClick = {
                            onMarkAsDone(task)
                        }, modifier = Modifier
                            .background(
                                CustomThemeManager.colors.primaryColor.copy(
                                    if (CustomThemeManager.isSystemDarkTheme()) {
                                        0.65f
                                    } else {
                                        0.35f
                                    }
                                )
                            )
                            .weight(1f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.mark_as_done),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if(CustomThemeManager.isSystemDarkTheme()){
                                    CustomThemeManager.colors.textColorOnPrimary
                                }else{
                                    CustomThemeManager.colors.primaryColor
                                },
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(
                                    CustomThemeManager.colors.primaryColor.copy(
                                        alpha = if (CustomThemeManager.isSystemDarkTheme()) {
                                            0.9f
                                        } else {
                                            0.6f
                                        }
                                    )
                                )
                                .width(1.dp)
                        )
                    }

                    TextButton(onClick = {
                        if(editedData){
                            onSave(editedModel)
                        }else{
                            onClose()
                        }
                    }, modifier = Modifier
                        .background(
                            CustomThemeManager.colors.primaryColor.copy(
                                if (CustomThemeManager.isSystemDarkTheme()) {
                                    0.65f
                                } else {
                                    0.35f
                                }
                            )
                        )
                        .weight(1f)
                    ) {
                        Text(
                            text = if(editedData) stringResource(id = R.string.save) else stringResource(id = R.string.ok),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if(CustomThemeManager.isSystemDarkTheme()){
                                CustomThemeManager.colors.textColorOnPrimary
                            }else{
                                CustomThemeManager.colors.primaryColor
                            },
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
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
        ), "asdasd", 1668419104L,
        onClose = {},
        onSave = {},
        onEditClick = {},
        onMarkAsDone = {})
    }
}