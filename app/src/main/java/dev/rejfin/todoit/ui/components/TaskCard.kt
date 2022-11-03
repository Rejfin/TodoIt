package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.TaskPartModel
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun TaskCard(task: TaskModel,
             modifier: Modifier = Modifier,
             onRemoveClick: (TaskModel) -> Unit= {},
             showRemoveButton:Boolean = true){
    Column(modifier = modifier
        .fillMaxWidth()
        .shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            clip = true
        )
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)
    ) {
        Row(verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
            Column() {
                Text(text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp),
                    color = if(task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.textColorFirst,
                    style = if(task.taskParts.all { it.status } && task.done) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                )
                Text(text = task.description,
                    color= CustomThemeManager.colors.textColorSecond,
                    modifier = Modifier.padding(8.dp),
                    style = if(task.taskParts.all { it.status } && task.done) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                )
            }
            if(showRemoveButton){
                IconButton(onClick = { onRemoveClick(task) }){
                    Icon(Icons.Default.Delete, stringResource(id = R.string.remove_task))
                }
            }
        }

        if(task.taskParts.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                task.taskParts.forEach { pair ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if (pair.status) {
                            Icon(imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "task done",
                                modifier = Modifier.size(20.dp),
                                tint = if (task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.doneColor
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Circle,
                                contentDescription = "task undone",
                                modifier = Modifier.size(20.dp),
                                tint = CustomThemeManager.colors.primaryColor
                            )
                        }
                        Text(
                            text = pair.desc,
                            color = if (pair.status) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.textColorFirst,
                            style = if (pair.status) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = task.taskParts.count { it.status } * 1f / task.taskParts.size,
                    color = if (task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorThird else CustomThemeManager.colors.primaryColor,
                    backgroundColor = CustomThemeManager.colors.textColorThird,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth()
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            if(!task.allDay){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "task time",
                        tint = CustomThemeManager.colors.textColorThird,
                    )
                    Text(text = "${String.format("%02d",task.startDate.hour)}:${String.format("%02d",task.startDate.minutes)} - ${String.format("%02d",task.endDate.hour)}:${String.format("%02d",task.endDate.minutes)}",
                        color = CustomThemeManager.colors.textColorSecond,
                        style = if(task.taskParts.all { it.status } && task.done) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                    )
                }
            }else{
                Box(){}
            }
            Text(
                text = "${task.xpForTask} xp",
                color = CustomThemeManager.colors.textColorSecond,
                style = if(task.taskParts.all { it.status } && task.done) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun TaskCardPreview(){
    var task = TaskModel()
    task = task.copy(
        title = "test",
        description = "asdasd",
        allDay = false,
        startDate = CustomDateFormat(2022, 12 ,2, 11, 25),
        endDate = CustomDateFormat(2022, 12 ,2, 14, 25),
        done = true,
        taskParts = listOf(
            TaskPartModel(false, "asdasd"),
            TaskPartModel(true, "fgjfn")
        )
    )
    TaskCard(task)
}