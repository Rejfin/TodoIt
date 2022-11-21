package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun InputDialog(
    message: String,
    errorMessage: String,
    onConfirmClick: (String)->Unit,
    onCancelClick: ()->Unit,
    text:String = "",
    allowedRegex: Regex? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    textStyle: TextStyle = TextStyle(textAlign = TextAlign.Center)
){
    var inputText by remember{ mutableStateOf(text)}
    var validator by remember { mutableStateOf(ValidationResult(errorMessage = errorMessage)) }
    
    BaseDialog(
        dialogType = DialogType.INPUT,
        onDismissRequest = {onCancelClick()},
        iconSize = 45.dp,
        content = {
            Column(modifier = Modifier.fillMaxWidth()){

                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = CustomThemeManager.colors.textColorFirst,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                )

                InputField(
                    text = inputText,
                    onTextChange = {
                        inputText = it
                        validator = validator.copy(isError = it.isEmpty())
                    },
                    validationResult = validator,
                    allowedRegex = allowedRegex,
                    keyboardType = keyboardType,
                    textStyle = textStyle,
                    imeAction = ImeAction.Done,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 4.dp)
                )

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .requiredHeight(if(validator.isError) 70.dp else 48.dp)
                ) {
                    TextButton(onClick = {
                        onCancelClick()
                    }, modifier = Modifier
                        .background(
                            CustomThemeManager.colors.errorColor.copy(
                                if (CustomThemeManager.isSystemDarkTheme()) {
                                    0.65f
                                } else {
                                    0.35f
                                }
                            )
                        )
                        .weight(1f)
                    ) {
                        Text(
                            stringResource(id = R.string.cancel),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if(CustomThemeManager.isSystemDarkTheme()){
                                CustomThemeManager.colors.textColorOnPrimary
                            }else{
                                CustomThemeManager.colors.errorColor
                            },
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }

                    TextButton(
                        enabled = !validator.isError,
                        onClick = {
                        if(!validator.isError){
                            onConfirmClick(inputText)
                        }
                    }, modifier = Modifier
                            .background(
                                CustomThemeManager.colors.primaryColor.copy(
                                    if (CustomThemeManager.isSystemDarkTheme()) {
                                        0.65f
                                    } else {
                                        0.35f
                                    }
                                )
                            )
                            .weight(1f)
                    ) {
                        Text(
                            stringResource(id = R.string.ok),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if(CustomThemeManager.isSystemDarkTheme()){
                                CustomThemeManager.colors.textColorOnPrimary
                            }else{
                                CustomThemeManager.colors.primaryColor
                            },
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
@Preview
fun InputDialogPreview(){
    CustomJetpackComposeTheme(customTheme = CustomTheme.LIGHT) {
        InputDialog(
            message = "test message with input field",
            errorMessage = "bad format",
            text = "",
            onConfirmClick = {},
            onCancelClick = {},
        )
    }
}