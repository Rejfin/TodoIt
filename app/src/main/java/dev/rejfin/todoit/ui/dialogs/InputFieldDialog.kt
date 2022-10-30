package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.InputField

@Composable
fun InputFieldDialog(text:String, onOkButtonClick: (String)->Unit, onCancelButtonClick: ()->Unit){
    var inputText by remember{ mutableStateOf("")}

    AlertDialog(
        onDismissRequest = {
            onOkButtonClick("")
        },
        title = {
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(text = text)
                InputField(
                    label = "",
                    onTextChange = {inputText = it},
                    validationResult = ValidationResult()
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {onOkButtonClick(inputText)}) {
                    Text(text = stringResource(id = R.string.ok))
                }
                Button(onClick = {onCancelButtonClick()}){
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}