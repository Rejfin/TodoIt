package dev.rejfin.todoit.models.states

import dev.rejfin.todoit.models.NotificationModel
import dev.rejfin.todoit.models.TrophyModel
import dev.rejfin.todoit.models.UserModel

data class ProfileUiState(
    val userData: UserModel = UserModel(),
    val isUserStillLoggedIn: Boolean = true,
    val showNotificationListDialog: Boolean = false,
    val notificationList: MutableList<NotificationModel> = mutableListOf(),
    val showLoadingDialog: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val trophyList: MutableList<TrophyModel> = mutableListOf()
)
