package dev.rejfin.todoit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskCard(task: TaskModel){
    val sdfDate = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)
    ) {
        Text(text = task.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp),
            color = if(task.taskParts.all { it.first }) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.textColorFirst,
            style = if(task.taskParts.all { it.first }) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
        )
        Text(text = task.description,
            color= CustomThemeManager.colors.textColorSecond,
            modifier = Modifier.padding(8.dp),
            style = if(task.taskParts.all { it.first }) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
        )
        if(task.taskParts.size > 1){
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                task.taskParts.forEach{ pair ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if(pair.first){
                            Icon(imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "task done",
                                modifier = Modifier.size(20.dp),
                                tint = if(task.taskParts.all { it.first }) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.doneColor
                            )
                        }else{
                            Icon(imageVector = Icons.Outlined.Circle,
                                contentDescription = "task undone",
                                modifier = Modifier.size(20.dp),
                                tint = CustomThemeManager.colors.primaryColor
                            )
                        }
                        Text(text = pair.second,
                            color = if(pair.first) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.textColorFirst,
                            style = if(pair.first) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = task.taskParts.count { it.first } * 1f/task.taskParts.size,
                    color = if(task.taskParts.all { it.first }) CustomThemeManager.colors.textColorThird else CustomThemeManager.colors.primaryColor,
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
            if(!task.isAllDay){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "task time",
                        tint = CustomThemeManager.colors.textColorThird,
                    )
                    Text(text = "${sdfDate.format(task.timestampStart!! * 1000L)} - ${sdfDate.format(task.timestampStop!! * 1000L)}",
                        color = CustomThemeManager.colors.textColorSecond,
                        style = if(task.taskParts.all { it.first }) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                    )
                }
            }else{
                Box(){}
            }
            Text(
                text = "${task.xpForTask} xp",
                color = CustomThemeManager.colors.textColorSecond,
                style = if(task.taskParts.all { it.first }) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun TaskCardPreview(){
    var task = TaskModel()
    task = task.copy(
        isAllDay = false,
        timestampStart = 1665507745L,
        timestampStop = 1665511345L,
        taskParts = listOf(
            Pair(true, "part 1"),
            Pair(false, "part 2"),
            Pair(true, "part 3"),
        )
    )
    TaskCard(task)
}