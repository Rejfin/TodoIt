package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.rejfin.todoit.R

@Composable
fun ErrorDialog(title:String, errorText: String, onDialogClose: () -> Unit = {}){
    var showDialog by remember{ mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDialogClose()
            },
            title = {
                Text(text = title, modifier = Modifier.fillMaxWidth())
            },
            text = {
                Text(text = errorText)
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { 
                            showDialog = false 
                            onDialogClose()
                        }
                    ) {
                        Text(stringResource(id = R.string.ok))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }



}

@Preview(showBackground = true)
@Composable
fun errorPreview(){
    ErrorDialog(title = "test title", errorText = "test error message")
}