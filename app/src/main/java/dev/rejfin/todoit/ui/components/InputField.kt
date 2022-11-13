package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputField(label:String,
               onTextChange: (String) -> Unit,
               validationResult: ValidationResult,
               modifier: Modifier = Modifier,
               placeholder: String = "",
               keyboardType: KeyboardType = KeyboardType.Text,
               imeAction: ImeAction = ImeAction.None,
               isPasswordField: Boolean = false,
               enabled:Boolean = true,
               singleLine: Boolean = true,
               rememberTextInternally: Boolean = true,
               textAlignment: TextAlign = TextAlign.Start){

    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf(placeholder) }
    var passwordVisibility by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally){
        OutlinedTextField(
            value=if (rememberTextInternally) text else placeholder,
            onValueChange = {
                if(rememberTextInternally){
                    text = it
                }
                onTextChange(it)
            },
            label = { Text(label) },
            modifier = modifier,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = CustomThemeManager.colors.cardBackgroundColor,
                focusedIndicatorColor = CustomThemeManager.colors.primaryColor,
                focusedLabelColor = CustomThemeManager.colors.primaryColor,
                cursorColor = CustomThemeManager.colors.primaryColor
            ),
            singleLine = singleLine,
            keyboardOptions = if(isPasswordField) KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction) else KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            visualTransformation = if (passwordVisibility || !isPasswordField) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(10.dp),
            isError = validationResult.isError,
            trailingIcon = if(isPasswordField) {{
                if(isPasswordField){
                    val image = if (passwordVisibility)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    val description = if (passwordVisibility) stringResource(id = R.string.hide_password) else stringResource(id = R.string.show_password)

                    IconButton(onClick = {passwordVisibility = !passwordVisibility}){
                        Icon(imageVector  = image, description)
                    }
                }
            }} else null,
            keyboardActions = KeyboardActions(
                onDone = {keyboardController?.hide()}
            ),
            enabled = enabled,
            textStyle = TextStyle(textAlign = textAlignment)
        )
        if(validationResult.isError){
            Text(text = validationResult.errorMessage!!, color = MaterialTheme.colors.error, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable()
private fun InputPreview(){
    val validRes = ValidationResult(isError = true, errorMessage = "some error")
    InputField(label = "test", onTextChange = { }, validationResult = validRes)
}