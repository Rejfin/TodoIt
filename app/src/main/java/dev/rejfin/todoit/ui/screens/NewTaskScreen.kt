package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import dev.rejfin.todoit.ui.components.ButtonRadioGroup
import dev.rejfin.todoit.ui.components.StepsProgressBar
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Destination
@Composable
fun NewTaskScreen(navigator: DestinationsNavigator?, viewModel: NewTaskViewModel = viewModel()){
    var uiState = viewModel.taskUiState
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isAllDay by remember{mutableStateOf(uiState.isAllDay)}

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        InputField(
            label = stringResource(id = R.string.task_title),
            onTextChange = {
                title = it
            },
            validationResult = uiState.taskTitle
        )
        InputField(
            label = stringResource(id = R.string.task_description),
            onTextChange = {
                description = it
            },
            validationResult = uiState.taskDescription
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .clickable {
                    //TODO 
                    println("new task part")
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
                .background(CustomThemeManager.colors.cardBackgroundColor, RoundedCornerShape(8.dp))
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
                    text = "12.10.2022",
                    color = CustomThemeManager.colors.textColorFirst,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = CustomThemeManager.colors.appBackground)
                        .padding(8.dp)
                        .fillMaxWidth(0.8f)
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
                    selected = 1,
                    onSelectChanged = {
                        uiState = uiState.copy(isAllDay = it == 0)
                        isAllDay = it == 0
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
                            text = "12:45",
                            color = CustomThemeManager.colors.textColorFirst,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = CustomThemeManager.colors.appBackground)
                                .padding(8.dp)
                                .fillMaxWidth()
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
                            text = "14:45",
                            color = CustomThemeManager.colors.textColorFirst,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = CustomThemeManager.colors.appBackground)
                                .padding(8.dp)
                                .fillMaxWidth()
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
                .background(CustomThemeManager.colors.cardBackgroundColor, RoundedCornerShape(8.dp))
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
                    uiState = uiState.copy(difficulty = it)
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
                    uiState = uiState.copy(timeConsuming = it)
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 12.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(bottom = 8.dp, start = 12.dp, end = 12.dp)
                .fillMaxWidth()
                .background(CustomThemeManager.colors.cardBackgroundColor, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = stringResource(id = R.string.reward_for_task),
                color = CustomThemeManager.colors.textColorThird,
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp)
            )
            Text(
                "- 250xp",
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
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = CustomThemeManager.colors.secondaryColor,
                )
            ) {
                Text("Anuluj", color = CustomThemeManager.colors.textColorOnPrimary)
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = CustomThemeManager.colors.primaryColor,
                )
            ) {
                Text("Add Task", color = CustomThemeManager.colors.textColorOnPrimary)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xF0F0F0)
@Composable
private fun PreviewNewTaskScreen(){
    CustomJetpackComposeTheme() {
        NewTaskScreen(navigator = null)
    }
}