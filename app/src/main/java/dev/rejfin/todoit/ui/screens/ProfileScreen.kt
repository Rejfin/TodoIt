package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.R
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.viewmodels.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.navigation.popUpTo
import dev.rejfin.todoit.models.NotificationType
import dev.rejfin.todoit.ui.dialogs.ErrorDialog
import dev.rejfin.todoit.ui.dialogs.InfoDialog
import dev.rejfin.todoit.ui.dialogs.LoadingDialog
import dev.rejfin.todoit.ui.dialogs.NotificationListDialog
import dev.rejfin.todoit.ui.screens.destinations.LoginScreenDestination
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme

@Destination
@Composable
fun ProfileScreen(navigator: DestinationsNavigator?, viewModel: ProfileViewModel = viewModel()){
    val uiState = viewModel.uiState
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(CustomThemeManager.colors.appBackground)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            Text(
                text = stringResource(id = R.string.profile),
                fontSize = 18.sp,
                color = CustomThemeManager.colors.textColorFirst,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Row(horizontalArrangement = Arrangement.End){
                IconButton(onClick = { viewModel.showNotificationList() }) {
                    Icon(Icons.Default.Notifications, stringResource(id = R.string.notification))
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Share, stringResource(id = R.string.share_profile))
                }
                IconButton(onClick = {
                    viewModel.logOutUser()
                }) {
                    Icon(Icons.Default.Logout, stringResource(id = R.string.logout))
                }

            }
        }
    }

    if(uiState.showNotificationListDialog){
        NotificationListDialog(notificationList = uiState.notificationList,
            onNotificationClick = {
                if(it.type == NotificationType.INVITATION){
                    viewModel.joinGroup(it.payload!!)
                }
            }
        ) {
            viewModel.hideNotificationList()
        }
    }

    if(uiState.showLoadingDialog){
        LoadingDialog()
    }

    if(uiState.errorMessage != null){
        ErrorDialog(title = stringResource(id = R.string.error), errorText = uiState.errorMessage){
            viewModel.clearError()
        }
    }
    
    if(uiState.infoMessage != null){
        InfoDialog(title = "Info", infoText = uiState.infoMessage, onDialogClose = {viewModel.clearInfo()})
    }

    if(!uiState.isUserStillLoggedIn){
        navigator?.navigate(LoginScreenDestination()){
            popUpTo(NavGraphs.root) {
                inclusive = true
            }
        }
    }
}
@Preview
@Composable
fun ProfileScreen_Preview(){
    CustomJetpackComposeTheme() {
        ProfileScreen(navigator = null)
    }
}