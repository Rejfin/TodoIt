package dev.rejfin.todoit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.rejfin.todoit.AuthViewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.components.AppLogo
import dev.rejfin.todoit.components.InputField
import dev.rejfin.todoit.ui.theme.TodoItTheme
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel = viewModel()){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val _uiState = viewModel.uiState
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Column{
            AppLogo(modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 45.dp)
                .align(Alignment.CenterHorizontally))
            InputField(label = stringResource(id = R.string.email), onTextChange = {email = it},_uiState.email ,keyboardType = KeyboardType.Email, modifier = Modifier.fillMaxWidth(0.7f))
            InputField(label = stringResource(id = R.string.password), onTextChange = {password = it},_uiState.password ,keyboardType = KeyboardType.Password,isPasswordField = true, modifier = Modifier.fillMaxWidth(0.7f))
            Button(onClick = {
                /*ToDO*/
            }, modifier = Modifier
                .align(Alignment.End)
                .widthIn(140.dp, 200.dp)
            ) {
                Text(text = stringResource(id = R.string.login))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    TodoItTheme {
        LoginScreen()
    }
}