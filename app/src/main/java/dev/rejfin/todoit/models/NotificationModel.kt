package dev.rejfin.todoit.models


data class NotificationModel(
    val id: String = "",
    val type: NotificationType = NotificationType.INFO,
    val text: String = "",
    val payload: Any? = null
)

enum class NotificationType{
    INVITATION, INFO
}
