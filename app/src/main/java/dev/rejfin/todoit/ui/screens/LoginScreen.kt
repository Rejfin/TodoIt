package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.viewmodels.AuthViewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.ui.components.AppLogo
import dev.rejfin.todoit.ui.components.InputField
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import dev.rejfin.todoit.ui.dialogs.CustomDialog
import dev.rejfin.todoit.ui.dialogs.DialogType
import dev.rejfin.todoit.ui.screens.destinations.ForgottenPasswordScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.HomeScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.LoginScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.RegisterScreenDestination
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Destination
@Composable
fun LoginScreen(navigator: DestinationsNavigator?, viewModel: AuthViewModel = viewModel()){
    val uiState = viewModel.loginUiState

    LaunchedEffect(Unit){
        if(viewModel.isUserAlreadyLoggedIn()){
            navigator?.navigate(HomeScreenDestination){
                popUpTo(LoginScreenDestination) {
                    inclusive = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Column(Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Column(Modifier.fillMaxWidth(0.7f)){
                AppLogo(modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 45.dp)
                    .align(Alignment.CenterHorizontally))
                InputField(
                    label = stringResource(id = R.string.email),
                    text = uiState.email.value,
                    onTextChange = { uiState.email.value = it },
                    validationResult = uiState.emailValidation.value,
                    keyboardType = KeyboardType.Email,
                    imeAction= ImeAction.Next,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isAuthInProgress.value,
                    allowedRegex = Regex("[^\\s]*")
                )
                InputField(
                    label = stringResource(id = R.string.password),
                    text = uiState.password.value,
                    onTextChange = {uiState.password.value = it},
                    validationResult = uiState.passwordValidation.value,
                    keyboardType = KeyboardType.Password,
                    imeAction= ImeAction.Done,
                    isPasswordField = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isAuthInProgress.value,
                    allowedRegex = Regex("[^\\s]*")
                )
                Text(
                    text = stringResource(id = R.string.forgotten_password),
                    color = CustomThemeManager.colors.primaryColor,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(bottom = 15.dp, top = 8.dp)
                        .fillMaxWidth()
                        .clickable(enabled = !uiState.isAuthInProgress.value) {
                            navigator?.navigate(ForgottenPasswordScreenDestination())
                        }
                )

                Button(onClick = {
                    viewModel.loginUserWithEmail()
                }, modifier = Modifier
                    .align(Alignment.End)
                    .widthIn(140.dp, 200.dp),
                    enabled = !uiState.isAuthInProgress.value,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = CustomThemeManager.colors.primaryColor)
                ) {
                    Text(text = stringResource(id = R.string.log_in),
                        color = CustomThemeManager.colors.textColorOnPrimary
                    )
                }

                if(uiState.isAuthInProgress.value){
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                if(uiState.authFailedMessage.value != null){
                    CustomDialog(
                        dialogType = DialogType.ERROR,
                        title = stringResource(id = R.string.log_in_error),
                        message = uiState.authFailedMessage.value!!,
                        onConfirmClick = {
                            viewModel.dismissAuthError()
                        }
                    )
                }
                if(uiState.isUserLoggedIn.value){
                    navigator?.navigate(HomeScreenDestination){
                        popUpTo(LoginScreenDestination) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(bottom = 100.dp, top = 600.dp)
                .align(Alignment.BottomCenter)){
            Text(stringResource(id = R.string.dont_have_acc),
                color = CustomThemeManager.colors.textColorSecond,
                fontSize = 15.sp
            )
            Text(
                stringResource(id = R.string.create_now),
                color = CustomThemeManager.colors.primaryColor,
                fontSize = 15.sp,
                modifier = Modifier.clickable(enabled = !uiState.isAuthInProgress.value) {
                    navigator?.navigate(RegisterScreenDestination){
                        popUpTo(LoginScreenDestination) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    CustomJetpackComposeTheme() {
        LoginScreen(navigator = null)
    }
}