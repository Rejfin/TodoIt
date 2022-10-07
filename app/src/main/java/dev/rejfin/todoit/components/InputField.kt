package dev.rejfin.todoit.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun InputField(label:String,
               onTextChange: (String) -> Unit,
               modifier: Modifier = Modifier,
               placeholder: String = "",
               keyboardType: KeyboardType = KeyboardType.Text,
               visualTransformation: VisualTransformation = VisualTransformation.None){

    var text by remember { mutableStateOf(placeholder) }

    OutlinedTextField(
        value=text,
        onValueChange = {
            text = it
            onTextChange(it)
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    )
}