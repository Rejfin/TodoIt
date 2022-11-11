package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.NotificationModel
import dev.rejfin.todoit.models.NotificationType
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun NotificationListDialog(notificationList: List<NotificationModel>, onNotificationClick: (NotificationModel) -> Unit, onClose: () -> Unit){
    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
        ),
        onDismissRequest = {
            onClose()
        },
        title = {
            Text(text = stringResource(id = R.string.notification_list), modifier = Modifier.fillMaxWidth())
        },
        text = {
            if(notificationList.isEmpty()){
                Text(text = stringResource(id = R.string.none_notification))
            }else{
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ){
                    items(items = notificationList,
                        key = {notification -> notification.id },
                        contentType = { TaskModel::class.java }
                    )
                    {notification ->
                        Text(
                            text = notification.text,
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CustomThemeManager.colors.cardBackgroundColor)
                                .clickable {
                                    onNotificationClick(notification)
                                }
                        )
                    }
                }
            }
        },
        buttons = {
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun NotificationList_Preview(){
    NotificationListDialog(
        listOf(
            NotificationModel("asd", NotificationType.INVITATION, "testasdasd", null),
            NotificationModel("a23sd", NotificationType.INVITATION, "testasdasd", null),
            NotificationModel("asr4d", NotificationType.INVITATION, "testasdasd", null),
            NotificationModel("as32d", NotificationType.INVITATION, "testasdasd", null)
        ),
        onNotificationClick = {},
        onClose = {}
    )
}