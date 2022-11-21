package dev.rejfin.todoit.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.TaskPartModel
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.utils.CalendarUtility

@Composable
fun TaskCard(task: TaskModel,
             userId: String,
             modifier: Modifier = Modifier,
             onRemoveClick: (TaskModel) -> Unit= {},
             showRemoveButton:Boolean = true,
             onBellClick: (TaskModel) -> Unit = {}
){

    val pref = LocalContext.current.getSharedPreferences("TodoItPref", ComponentActivity.MODE_PRIVATE)
    val beforeTime = pref.getInt("notification_time", 15)
    var bellClicked by remember { mutableStateOf(pref.contains(task.id) && !task.done && task.startTimestamp - (beforeTime*60*1000) > CalendarUtility().getCurrentTimestamp())}
    val bellTint = if(bellClicked) CustomThemeManager.colors.secondaryColor else CustomThemeManager.colors.textColorThird
    val calendar by remember { mutableStateOf(CalendarUtility()) }

    Row(modifier = modifier
        .fillMaxWidth()
        .shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            clip = true
        )
        .clip(RoundedCornerShape(8.dp))
        .background(CustomThemeManager.colors.cardBackgroundColor)
        .height(IntrinsicSize.Min)
    ){

        if(task.done){
            Text(
                text = stringResource(id = R.string.done),
                textAlign = TextAlign.Center,
                color = CustomThemeManager.colors.textColorOnPrimary,
                modifier = Modifier
                    .rotateVertically(-90f)
                    .background(CustomThemeManager.colors.doneColor)
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .align(CenterVertically)
            )
        }else if(task.endTimestamp < System.currentTimeMillis()){
            Text(
                text = stringResource(id = R.string.undone),
                textAlign = TextAlign.Center,
                color = CustomThemeManager.colors.textColorOnPrimary,
                modifier = Modifier
                    .rotateVertically(-90f)
                    .background(CustomThemeManager.colors.textColorSecond.copy(alpha = 0.9f))
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .align(CenterVertically)
            )
        }else if(task.lockedByUserId != null && task.lockedByUserId != userId){
            Text(
                text = stringResource(id = R.string.locked),
                textAlign = TextAlign.Center,
                color = CustomThemeManager.colors.textColorOnPrimary,
                modifier = Modifier
                    .rotateVertically(-90f)
                    .background(CustomThemeManager.colors.textColorSecond.copy(alpha = 0.9f))
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .align(CenterVertically)
            )
        }

        Column(modifier = modifier
            .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Column{
                    Text(text = task.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp),
                        color = if(task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.textColorFirst,
                        style = if(task.taskParts.all { it.status } && task.done) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                    )
                    Text(text = task.description,
                        color= CustomThemeManager.colors.textColorSecond,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(8.dp),
                        style = if(task.taskParts.all { it.status } && task.done) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                    )
                }
                Row{
                    if(task.lockedByUserId != null && task.lockedByUserId != userId && !task.done){
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(id = R.string.task_locked),
                            tint = CustomThemeManager.colors.textColorThird,
                            modifier = Modifier.padding(top = 11.dp)
                        )
                    }
                    if(showRemoveButton){
                        IconButton(onClick = { onRemoveClick(task) }){
                            Icon(Icons.Default.Delete, stringResource(id = R.string.remove_task))
                        }
                    }
                }
            }

            if(task.taskParts.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    task.taskParts.forEachIndexed {index, pair ->
                        if(index == 6){
                            Text(
                                text = "...",
                                color = if (pair.status) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.textColorFirst,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }else if(index < 6){
                            Row(modifier = Modifier.fillMaxWidth()) {
                                if (pair.status) {
                                    Icon(imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = stringResource(id = R.string.task_done),
                                        modifier = Modifier.size(20.dp),
                                        tint = if (task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.doneColor
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = stringResource(id = R.string.task_undone),
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
                    }
                    LinearProgressIndicator(
                        progress = task.taskParts.count { it.status } * 1f / task.taskParts.size,
                        color = if (task.taskParts.all { it.status } && task.done) CustomThemeManager.colors.textColorThird else CustomThemeManager.colors.primaryColor,
                        backgroundColor = CustomThemeManager.colors.primaryColor.copy(alpha = 0.3f),
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
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ){
                if(!task.allDay){
                    Row(
                        verticalAlignment = CenterVertically,
                        modifier = Modifier.clickable {
                            if(!task.done && task.startTimestamp - (beforeTime*60*1000) > CalendarUtility().getCurrentTimestamp()){
                                onBellClick(task)
                                bellClicked = !bellClicked
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = stringResource(id = R.string.task_time),
                            tint = bellTint,
                        )
                        Text(text = "${calendar.timestampToHourString(task.startTimestamp)} - ${calendar.timestampToHourString(task.endTimestamp)}",
                            color = CustomThemeManager.colors.textColorSecond,
                            style = if(task.taskParts.all { it.status } && task.done) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                        )
                    }
                }else{
                    Box{}
                }
                Text(
                    text = "${task.xpForTask} xp",
                    color = CustomThemeManager.colors.textColorSecond,
                    style = if(task.taskParts.all { it.status } && task.done) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                )
            }
        }
    }
}

fun Modifier.rotateVertically(rotation: Float) = then(
    object : LayoutModifier {
        override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
            val placeable = measurable.measure(constraints)
            return layout(placeable.height, placeable.width) {
                placeable.place(
                    x = -(placeable.width / 2 - placeable.height / 2),
                    y = -(placeable.height / 2 - placeable.width / 2)
                )
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int): Int {
            return measurable.maxIntrinsicWidth(width)
        }

        override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurable: IntrinsicMeasurable, width: Int): Int {
            return measurable.maxIntrinsicWidth(width)
        }

        override fun IntrinsicMeasureScope.minIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int): Int {
            return measurable.minIntrinsicHeight(height)
        }

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurable: IntrinsicMeasurable, height: Int): Int {
            return measurable.maxIntrinsicHeight(height)
        }
    })
    .then(rotate(rotation))


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
        done = false,
        taskParts = listOf(
            TaskPartModel(false, "asdasd"),
            TaskPartModel(true, "fgjfn")
        )
    )
    TaskCard(task, "asdad")
}