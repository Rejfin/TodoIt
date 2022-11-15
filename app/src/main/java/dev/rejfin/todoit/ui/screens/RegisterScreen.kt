package dev.rejfin.todoit.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import dev.rejfin.todoit.viewmodels.AuthViewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.ui.components.AppLogo
import dev.rejfin.todoit.ui.dialogs.ErrorDialog
import dev.rejfin.todoit.ui.dialogs.InfoDialog
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.screens.destinations.LoginScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.RegisterScreenDestination
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Destination
@Composable
fun RegisterScreen(navigator: DestinationsNavigator?, viewModel: AuthViewModel = viewModel()) {
    val uiState = viewModel.registerUiState

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uiState.selectedImage.value = uri ?: Uri.EMPTY
        }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(Modifier
            .fillMaxWidth(0.7f)
            .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            AppLogo(modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 20.dp)
                .align(CenterHorizontally))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.selectedImage.value)
                    .crossfade(true)
                    .build(),
                placeholder = rememberVectorPainter(Icons.Filled.Person),
                contentDescription = stringResource(id = R.string.profile_image),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Filled.Person),
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .size(120.dp, 120.dp)
                    .align(CenterHorizontally)
                    .clickable {
                        galleryLauncher.launch("image/*")
                    }
            )
            InputField(
                label = stringResource(id = R.string.nick),
                text = uiState.nick.value,
                onTextChange = {
                    uiState.nick.value = it
                    viewModel.clearError(uiState.nickValidation.value)
                },
                validationResult = uiState.nickValidation.value,
                imeAction= ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress.value,
                allowedRegex = Regex("[^\\s]*")
            )
            InputField(
                label = stringResource(id = R.string.display_name),
                text = uiState.displayName.value,
                onTextChange = {
                    uiState.displayName.value = it
                    viewModel.clearError(uiState.displayNameValidation.value)
                },
                validationResult = uiState.displayNameValidation.value,
                imeAction= ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress.value
            )
            InputField(
                label = stringResource(id = R.string.email),
                text = uiState.email.value,
                onTextChange = {
                    uiState.email.value = it
                    viewModel.clearError(uiState.emailValidation.value)
                },
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
                onTextChange = {
                    uiState.password.value = it
                    viewModel.clearError(uiState.passwordValidation.value)
                },
                validationResult = uiState.passwordValidation.value,
                keyboardType = KeyboardType.Password,
                imeAction= ImeAction.Next,
                isPasswordField = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress.value,
                allowedRegex = Regex("[^\\s]*")
            )
            InputField(
                label = stringResource(id = R.string.repeat_password),
                text = uiState.repeatedPassword.value,
                onTextChange = {
                    uiState.repeatedPassword.value = it
                    viewModel.clearError(uiState.repeatedPasswordValidation.value)
                },
                validationResult = uiState.repeatedPasswordValidation.value,
                keyboardType = KeyboardType.Password,
                imeAction= ImeAction.Done,
                isPasswordField = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress.value,
                allowedRegex = Regex("[^\\s]*")
            )
            Row(modifier = Modifier
                .padding(vertical = 15.dp)
                .align(CenterHorizontally)){
                Text(stringResource(id = R.string.have_acc),
                    fontSize = 15.sp,
                    color = CustomThemeManager.colors.textColorSecond
                )
                Text(
                    stringResource(id = R.string.log_in_now),
                    color = CustomThemeManager.colors.primaryColor,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable(enabled = !uiState.isAuthInProgress.value){
                    navigator?.navigate(LoginScreenDestination) {
                        popUpTo(RegisterScreenDestination) {
                            inclusive = true
                        }
                    }
                }
                )
            }
            Button(
                onClick = {
                    viewModel.registerUserWithEmail()
                }, modifier = Modifier
                    .align(Alignment.End)
                    .widthIn(140.dp, 200.dp),
                enabled = !uiState.isAuthInProgress.value,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = CustomThemeManager.colors.primaryColor)
            ) {
                Text(
                    text = stringResource(id = R.string.register),
                    color = CustomThemeManager.colors.textColorOnPrimary
                )
            }
            if(uiState.isAuthInProgress.value){
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if(uiState.authFailedMessage.value != null){
                ErrorDialog(title = stringResource(id = R.string.register_error), errorText = uiState.authFailedMessage.value!!, onDialogClose = {
                    viewModel.dismissAuthError()
                })
            }
            if(uiState.registerSuccess.value){
                InfoDialog(
                    title = stringResource(id = R.string.register_success),
                    infoText = stringResource(id = R.string.register_success_message),
                    onDialogClose = {
                        navigator?.navigate(LoginScreenDestination){
                            popUpTo(RegisterScreenDestination) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterPreview() {
    CustomJetpackComposeTheme {
        RegisterScreen(null)
    }
}