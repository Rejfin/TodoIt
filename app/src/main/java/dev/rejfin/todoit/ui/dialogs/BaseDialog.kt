package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import dev.rejfin.todoit.ui.theme.CustomThemeManager

enum class DialogType{INFO, ERROR, DECISION, SETTINGS, NOTIFICATION_LIST, INPUT, TASK_DETAILS}

@Composable
fun BaseDialog(
    dialogType: DialogType,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
    dismissOnClickOutside: Boolean = false,
    iconSize: Dp = 60.dp
){
    val questionIcon by remember { mutableStateOf(Icons.Outlined.QuestionMark) }
    val errorIcon by remember { mutableStateOf(Icons.Outlined.ErrorOutline) }
    val infoIcon by remember { mutableStateOf(Icons.Outlined.Info) }
    val settingsIcon by remember { mutableStateOf(Icons.Filled.Settings) }
    val notificationIcon by remember { mutableStateOf(Icons.Outlined.Notifications) }
    val taskDetailsIcon by remember { mutableStateOf(Icons.Outlined.Checklist) }
    val inputIcon by remember { mutableStateOf(Icons.Outlined.Edit) }
    var infoColor by remember { mutableStateOf(Color.Transparent) }
    var errorColor by remember { mutableStateOf(Color.Transparent) }

    Dialog(
        onDismissRequest = {
            onDismissRequest()
        },
        properties = DialogProperties(
            dismissOnBackPress = dismissOnClickOutside,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        infoColor = CustomThemeManager.colors.primaryColor
        errorColor = CustomThemeManager.colors.errorColor

        Column(horizontalAlignment = CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(Color.Transparent)
                        .align(Alignment.TopCenter)
                        .zIndex(1f)
                )
                Card(shape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 12.dp),
                    backgroundColor = CustomThemeManager.colors.cardBackgroundColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, bottom = 0.dp)
                        .zIndex(1f)
                ) {
                    Column{
                        Spacer(modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth()
                            .background(
                                when (dialogType) {
                                    DialogType.INFO -> infoColor
                                    DialogType.ERROR -> errorColor
                                    else -> infoColor
                                }
                            )
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Box {
                            content()
                        }
                    }

                }

                Box(
                    contentAlignment = Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(2f)
                ){
                    Box(modifier = Modifier
                        .padding(top = 30.dp)
                        .size(65.dp)
                        .clip(CircleShape)
                        .background(
                            when (dialogType) {
                                DialogType.INFO -> infoColor
                                DialogType.ERROR -> errorColor
                                else -> infoColor
                            }
                        )

                    ){
                        Icon(
                            imageVector = when(dialogType){
                                DialogType.DECISION -> questionIcon
                                DialogType.INFO -> infoIcon
                                DialogType.ERROR -> errorIcon
                                DialogType.SETTINGS -> settingsIcon
                                DialogType.NOTIFICATION_LIST -> notificationIcon
                                DialogType.INPUT -> inputIcon
                                DialogType.TASK_DETAILS-> taskDetailsIcon
                                                          },
                            contentDescription = dialogType.name,
                            tint = Color.White,
                            modifier = Modifier
                                .size(iconSize)
                                .zIndex(3f)
                                .align(Center)
                        )
                    }
                }
            }
        }
    }
}