package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.InputField

@Composable
fun DelaySetterDialog(initTime: String, onDialogClose: () -> Unit, onTimeSet: (timeInMillis: Int?) -> Unit){

    val errorMessage = stringResource(
        id = R.string.only_numbers
    )
    var timeInString by remember { mutableStateOf(initTime) }
    var timeStringValidation by remember { mutableStateOf(ValidationResult(errorMessage = errorMessage))}
    
    fun validateInput(input: String){
        val mInput = input.toIntOrNull()
        timeStringValidation = if(mInput == null){
            timeStringValidation.copy(isError = true)
        }else{
            timeStringValidation.copy(isError = false)
        }
    }

    AlertDialog(
        onDismissRequest = {
            onDialogClose()
        },
        title = {
            Text(text = stringResource(id = R.string.notification_time), modifier = Modifier.fillMaxWidth())
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.notification_time_text))
                InputField(
                    label = "",
                    onTextChange = {
                        timeInString = it
                        validateInput(it)
                    },
                    validationResult = timeStringValidation,
                    modifier = Modifier.widthIn(max = 70.dp),
                    text = timeInString,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    textStyle = TextStyle(textAlign = TextAlign.Center)
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    onClick = {
                        onDialogClose()
                    }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    onClick = {
                        if(!timeStringValidation.isError){
                            onTimeSet(timeInString.toIntOrNull())
                        }
                    },
                    enabled = !timeStringValidation.isError
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
@Preview
fun DelaySetterDialogPreview(){
    DelaySetterDialog("15", {}, {})
}