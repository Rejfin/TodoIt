package dev.rejfin.todoit.ui.dialogs

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
fun InfoDialog(title:String, infoText: String,
               isDecisionDialog: Boolean = false,
               onDialogClose: () -> Unit = {},
               onConfirm: () -> Unit = {},
               onCancel: () -> Unit = {}){
    AlertDialog(
        onDismissRequest = {
            onDialogClose()
        },
        title = {
            Text(text = title, modifier = Modifier.fillMaxWidth())
        },
        text = {
            Text(text = infoText)
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if(isDecisionDialog){
                    Button(
                        modifier = Modifier.weight(1f).padding(4.dp),
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
                Button(
                    modifier = Modifier.weight(1f).padding(4.dp),
                    onClick = {
                        onConfirm()
                        if(!isDecisionDialog){
                            onDialogClose()
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun InfoPreview(){
    InfoDialog(title = "test title", infoText = "test error message")
}