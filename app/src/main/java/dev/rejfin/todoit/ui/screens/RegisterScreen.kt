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
import androidx.compose.material.icons.filled.Group
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
    var nick by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf(Uri.EMPTY) }
    var repeatedPassword by remember { mutableStateOf("") }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImage = uri
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
                    .data(selectedImage)
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
                onTextChange = {
                    nick = it
                    viewModel.clearError(uiState.nick)
                },
                uiState.nick,
                imeAction= ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress
            )
            InputField(
                label = stringResource(id = R.string.display_name),
                onTextChange = {
                    displayName = it
                    viewModel.clearError(uiState.displayName)
                },
                uiState.displayName,
                imeAction= ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress
            )
            InputField(
                label = stringResource(id = R.string.email),
                onTextChange = {
                    email = it
                    viewModel.clearError(uiState.email)
                },
                uiState.email,
                keyboardType = KeyboardType.Email,
                imeAction= ImeAction.Next,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress
            )
            InputField(
                label = stringResource(id = R.string.password),
                onTextChange = {
                    password = it
                    viewModel.clearError(uiState.password)
                },
                uiState.password,
                keyboardType = KeyboardType.Password,
                imeAction= ImeAction.Next,
                isPasswordField = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress
            )
            InputField(
                label = stringResource(id = R.string.repeat_password),
                onTextChange = {
                    repeatedPassword = it
                    viewModel.clearError(uiState.repeatedPassword)
                },
                uiState.repeatedPassword,
                keyboardType = KeyboardType.Password,
                imeAction= ImeAction.Done,
                isPasswordField = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isAuthInProgress
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
                    modifier = Modifier.clickable(enabled = !uiState.isAuthInProgress){
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
                    viewModel.registerUserWithEmail(nick, displayName, email, password, repeatedPassword, selectedImage)
                }, modifier = Modifier
                    .align(Alignment.End)
                    .widthIn(140.dp, 200.dp),
                enabled = !uiState.isAuthInProgress,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = CustomThemeManager.colors.primaryColor)
            ) {
                Text(
                    text = stringResource(id = R.string.register),
                    color = CustomThemeManager.colors.textColorOnPrimary
                )
            }
            if(uiState.isAuthInProgress){
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if(uiState.authFailedMessage != null){
                ErrorDialog(title = stringResource(id = R.string.register_error), errorText = uiState.authFailedMessage, onDialogClose = {
                    viewModel.dismissAuthError()
                })
            }
            if(uiState.registerSuccess){
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
    CustomJetpackComposeTheme() {
        RegisterScreen(null)
    }
}