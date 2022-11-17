package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.R
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.viewmodels.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.navigation.popUpTo
import dev.rejfin.todoit.models.TrophyModel
import dev.rejfin.todoit.ui.components.CustomImage
import dev.rejfin.todoit.ui.components.TrophyCard
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
    val mContext = LocalContext.current
    val nickMessage by remember { mutableStateOf(mContext.getString(R.string.user_nick_dialog_text, uiState.userData.nick)) }

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
                .background(CustomThemeManager.colors.cardBackgroundColor)
        ) {
            Text(
                text = stringResource(id = R.string.profile),
                fontSize = 18.sp,
                color = CustomThemeManager.colors.textColorFirst,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            Row(horizontalArrangement = Arrangement.End){
                IconButton(onClick = { uiState.showNotificationListDialog = true }) {
                    Icon(Icons.Default.Notifications, stringResource(id = R.string.notification))
                }
                IconButton(onClick = {
                    uiState.infoMessage = nickMessage
                }) {
                    Icon(Icons.Default.Share, stringResource(id = R.string.share_profile))
                }
                IconButton(onClick = {
                    viewModel.logOutUser()
                }) {
                    Icon(Icons.Default.Logout, stringResource(id = R.string.logout))
                }
            }
        }

        CustomImage(
            imageUrl = uiState.userData.imageUrl,
            contentDescription = uiState.userData.displayName,
            size = DpSize(120.dp, 120.dp),
            placeholder = rememberVectorPainter(Icons.Filled.Person)
        )

        Text(
            text = uiState.userData.displayName,
            fontSize = 23.sp,
            fontWeight = FontWeight.W600,
            color = CustomThemeManager.colors.textColorFirst
        )

        Column{
            Text(
                text = "Lvl. ${uiState.userData.xp / 150 + 1} (${uiState.userData.xp - (uiState.userData.xp/150 * 150)}/${150 * (uiState.userData.xp/150 + 1)}xp)",
                color = CustomThemeManager.colors.textColorSecond
            )
            LinearProgressIndicator(
                progress = (uiState.userData.xp - (uiState.userData.xp/150 * 150)) / (150f * (uiState.userData.xp/150 + 1)),
                color = CustomThemeManager.colors.primaryColor,
                modifier = Modifier
                    .height(9.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
        }

        Text(
            text = stringResource(id = R.string.achievement_list),
            color = CustomThemeManager.colors.textColorFirst,
            modifier = Modifier
                .padding(top = 24.dp, start = 8.dp)
                .align(Start)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(items = uiState.trophyList,
                key = { trophy -> trophy.id },
                contentType = { TrophyModel::class.java }
            )
            { trophy ->
                TrophyCard(trophyModel = trophy)
            }
        }
    }

    if(uiState.showNotificationListDialog){
        NotificationListDialog(notificationList = uiState.notificationList,
            onNotificationClick = {
                viewModel.joinGroup(it.groupId, it.id)
            }
        ) {
            uiState.showNotificationListDialog = false
        }
    }

    if(uiState.showLoadingDialog){
        LoadingDialog()
    }

    if(uiState.errorMessage != null){
        ErrorDialog(title = stringResource(id = R.string.error), errorText = uiState.errorMessage!!){
            uiState.errorMessage = null
        }
    }

    if(uiState.infoMessage != null){
        InfoDialog(title = "Info",
            infoText = uiState.infoMessage!!,
            onDialogClose = {
                uiState.infoMessage = null
            }
        )
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
fun ProfileScreenPreview(){
    CustomJetpackComposeTheme {
        ProfileScreen(navigator = null)
    }
}