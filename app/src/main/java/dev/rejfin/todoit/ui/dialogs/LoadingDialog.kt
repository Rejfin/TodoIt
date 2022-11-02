package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingDialog(text:String = ""){
    AlertDialog(
        onDismissRequest = {
        },
        title = {
            Text(text = text, modifier = Modifier.fillMaxWidth())
        },
        text = {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(0.8f))
        },
        buttons = {
        },
        modifier = Modifier.fillMaxWidth()
    )
}