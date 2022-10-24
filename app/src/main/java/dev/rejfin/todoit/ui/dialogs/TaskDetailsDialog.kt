package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
fun TaskDetailsDialog(task: TaskModel, modifier: Modifier = Modifier, onClose: () -> Unit = {}){
    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
        ),
        onDismissRequest = {
            onClose()
        },
        title = {
            Text(text = "Task Details", modifier = Modifier.fillMaxWidth())
        },
        text = {
            Column(modifier = modifier
                .fillMaxWidth(0.8f)
                .background(CustomThemeManager.colors.cardBackgroundColor)
            ) {
                Text(
                    text = stringResource(id = R.string.task_title),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = task.title,
                    color = CustomThemeManager.colors.textColorFirst,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.task_description),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = task.description,
                    color = CustomThemeManager.colors.textColorFirst,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.reward_for_task),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = "- ${task.xpForTask}xp",
                    color = CustomThemeManager.colors.textColorFirst,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.day_of_completion),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = String.format("%02d.%02d.%d",task.startDate.day, task.startDate.month, task.startDate.year),
                    color = CustomThemeManager.colors.textColorFirst,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                if(!task.allDay){
                    Row() {
                        Text(
                            text = String.format("%02d:%02d - ",task.startDate.hour, task.startDate.minutes),
                            color = CustomThemeManager.colors.textColorFirst,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                        )
                        Text(
                            text = String.format("%02d:%02d",task.endDate.hour, task.endDate.minutes),
                            color = CustomThemeManager.colors.textColorFirst,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .padding(end = 8.dp)
                        )
                    }
                }

                if(task.taskParts.isNotEmpty()){
                    Text(
                        text = stringResource(id = R.string.task_parts),
                        color = CustomThemeManager.colors.textColorSecond,
                        textAlign = TextAlign.Start,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                            .fillMaxWidth()
                    )
                }

                task.taskParts.forEach {
                    Row(
                        verticalAlignment = CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        if(it.status){
                            Icon(imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "task done",
                                modifier = Modifier.size(12.dp),
                                tint = if(task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.doneColor
                            )
                        }else{
                            Icon(imageVector = Icons.Outlined.Circle,
                                contentDescription = "task undone",
                                modifier = Modifier.size(12.dp),
                                tint = CustomThemeManager.colors.primaryColor
                            )
                        }
                        Text(text = it.desc,
                            fontSize = 14.sp,
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
                Button(onClick = {
                    onClose()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.ok))
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
            id = "asdasd",
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
            done = false
        ))
    }
}