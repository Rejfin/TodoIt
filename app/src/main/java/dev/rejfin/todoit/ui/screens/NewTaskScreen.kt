package dev.rejfin.todoit.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.viewmodels.NewTaskViewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.ui.components.ButtonRadioGroup
import dev.rejfin.todoit.ui.components.ErrorDialog
import dev.rejfin.todoit.ui.components.StepsProgressBar
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Destination
@Composable
fun NewTaskScreen(navigator: DestinationsNavigator?, viewModel: NewTaskViewModel = viewModel()){
    val uiState = viewModel.taskUiState
    val calendarUtility = viewModel.calendarUtility

    val mContext = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()){
        Column(
            Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            InputField(
                label = stringResource(id = R.string.task_title),
                onTextChange = {
                    viewModel.updateTitle(it)
                },
                validationResult = uiState.taskTitleValidation,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            InputField(
                label = stringResource(id = R.string.task_description),
                onTextChange = {
                    viewModel.updateDescription(it)
                },
                validationResult = uiState.taskDescriptionValidation,
                imeAction = ImeAction.Done,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(min = 110.dp)
            )
            if(uiState.taskParts.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.task_parts),
                    color = CustomThemeManager.colors.textColorSecond,
                    modifier = Modifier.padding(top = 8.dp)
                )

                uiState.taskParts.forEachIndexed { index, _ ->
                    Row{
                        OutlinedTextField(
                            value = uiState.taskParts[index].desc,
                            onValueChange = {
                                viewModel.updateTaskPart(index, it)
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = CustomThemeManager.colors.cardBackgroundColor
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.removeTaskPart(index)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(id = R.string.remove_task_part)
                                    )
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .clickable {
                        viewModel.addTaskPart("")
                    }
            ) {
                Icon(imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.new_task_part),
                    tint = CustomThemeManager.colors.textColorSecond,
                    modifier = Modifier.size(28.dp)
                )
                Text(text = stringResource(id = R.string.new_task_part),
                    color = CustomThemeManager.colors.textColorSecond,
                    fontWeight = FontWeight.W500
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(
                        CustomThemeManager.colors.cardBackgroundColor,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.day_of_completion),
                        color = CustomThemeManager.colors.textColorThird,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${String.format("%02d",uiState.startDate.day)}.${String.format("%02d",uiState.startDate.month)}.${uiState.startDate.year}",
                        color = CustomThemeManager.colors.textColorFirst,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = CustomThemeManager.colors.appBackground)
                            .padding(8.dp)
                            .fillMaxWidth(0.8f)
                            .clickable {
                                showDatePicker(mContext, uiState.startDate) {
                                    viewModel.updateDayOfTask(it)
                                }
                            }
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.is_all_day),
                        color = CustomThemeManager.colors.textColorThird,
                    )
                    ButtonRadioGroup(
                        options = mapOf(0 to "yes", 1 to "no"),
                        selected = if(uiState.isAllDay) 0 else 1,
                        onSelectChanged = {
                            viewModel.updateIsAllDay(it == 0)
                        }
                    )
                }

                if(!uiState.isAllDay){
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(0.8f)
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.start_time),
                                color = CustomThemeManager.colors.textColorThird,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                            )
                            Text(
                                text = "${String.format("%02d",uiState.startDate.hour)}:${String.format("%02d",uiState.startDate.minutes)}",
                                color = CustomThemeManager.colors.textColorFirst,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color = CustomThemeManager.colors.appBackground)
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        showTimePicker(
                                            mContext,
                                            calendarUtility.getCurrentDate()
                                        ) { date ->
                                            viewModel.updateStartHour(date)
                                        }
                                    }
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(0.8f)
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.stop_time),
                                color = CustomThemeManager.colors.textColorThird,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                            )
                            Text(
                                text = "${String.format("%02d",uiState.endDate.hour)}:${String.format("%02d",uiState.endDate.minutes)}",
                                color = CustomThemeManager.colors.textColorFirst,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color = CustomThemeManager.colors.appBackground)
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        showTimePicker(
                                            mContext,
                                            calendarUtility.getCurrentDate()
                                        ) { date ->
                                            viewModel.updateEndHour(date)
                                        }
                                    }
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .fillMaxWidth()
                    .background(
                        CustomThemeManager.colors.cardBackgroundColor,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Text(text = stringResource(id = R.string.difficulty_level)+": ${uiState.difficulty}",
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 12.dp)
                )
                StepsProgressBar(
                    numberOfSteps = 5,
                    currentStep = uiState.difficulty,
                    onSelectionChanged = {
                        viewModel.updateDifficulty(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 12.dp)
                )

                Text(
                    text = stringResource(id = R.string.time_consuming)+": ${uiState.timeConsuming}",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                StepsProgressBar(
                    numberOfSteps = 5,
                    currentStep = uiState.timeConsuming,
                    onSelectionChanged = {
                        viewModel.updateTimeConsuming(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 12.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .background(
                        CustomThemeManager.colors.cardBackgroundColor,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = stringResource(id = R.string.reward_for_task),
                    color = CustomThemeManager.colors.textColorThird,
                    modifier = Modifier
                        .padding(start = 12.dp, top = 12.dp)
                )
                Text(
                    "${uiState.xpForCompleteTask} xp",
                    modifier = Modifier
                        .padding(start = 12.dp, bottom = 12.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ){
                Button(
                    onClick = {
                        navigator?.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = CustomThemeManager.colors.secondaryColor,
                    )
                ) {
                    Text(
                        stringResource(id = R.string.cancel),
                        color = CustomThemeManager.colors.textColorOnPrimary
                    )
                }
                Button(
                    onClick = {
                        viewModel.createTask()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = CustomThemeManager.colors.primaryColor,
                    )
                ) {
                    Text(
                        stringResource(id = R.string.add_task),
                        color = CustomThemeManager.colors.textColorOnPrimary
                    )
                }
            }
        }
        if(uiState.isDataSending){
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.6f))
                .align(Alignment.Center)
            ){
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(width = 140.dp, height = 140.dp)
                        .align(Alignment.Center)
                )
            }
        }
        if(uiState.isDateSent != null && uiState.isDateSent){
            navigator?.popBackStack()
        }
        if(uiState.taskErrorMessage != null){
            ErrorDialog(title = stringResource(id = R.string.error), errorText = uiState.taskErrorMessage) {
                viewModel.clearError()
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xF0F0F0)
@Composable
private fun PreviewNewTaskScreen(){
    CustomJetpackComposeTheme{
        NewTaskScreen(navigator = null)
    }
}

private fun showTimePicker(context: Context,
                           initTime: CustomDateFormat,
                           onUpdateEnd: (CustomDateFormat) -> Unit){
    // Creating a TimePicker dialog
    val mTimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            onUpdateEnd(CustomDateFormat(hour = mHour, minutes = mMinute))
        },
        initTime.hour,
        initTime.minutes,
        false
    )
    mTimePickerDialog.show()
}

private fun showDatePicker(context: Context, selectedDate: CustomDateFormat, onDateChanged: (CustomDateFormat) -> Unit){
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            onDateChanged(CustomDateFormat(year = mYear, month = mMonth, day = mDayOfMonth))
        }, selectedDate.year,
        selectedDate.month,
        selectedDate.day,
    )

    mDatePickerDialog.show()
}