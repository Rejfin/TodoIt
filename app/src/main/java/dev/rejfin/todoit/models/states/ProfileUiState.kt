package dev.rejfin.todoit.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import dev.rejfin.todoit.models.NotificationModel
import dev.rejfin.todoit.models.TrophyModel
import dev.rejfin.todoit.models.UserModel

class ProfileUiState{
    var userData by mutableStateOf(UserModel())
    var isUserStillLoggedIn by mutableStateOf(true)
    var showNotificationListDialog by mutableStateOf(false)
    var notificationList = mutableListOf<NotificationModel>()
    var showLoadingDialog by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var trophyList = mutableListOf<TrophyModel>()
    var infoMessage by mutableStateOf<String?>(null)
}
