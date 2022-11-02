package dev.rejfin.todoit.models.states

import dev.rejfin.todoit.models.NotificationModel

data class ProfileUiState(
    val isUserStillLoggedIn: Boolean = true,
    val showNotificationListDialog: Boolean = false,
    val notificationList: MutableList<NotificationModel> = mutableListOf(),
    val showLoadingDialog: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)
