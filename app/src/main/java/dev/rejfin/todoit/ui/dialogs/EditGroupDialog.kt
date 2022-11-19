package dev.rejfin.todoit.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.GroupModel
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.SmallUserModel
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.components.MemberCard
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun EditGroupDialog(
    groupData: GroupModel,
    userId: String,
    onSaveClick: (name: String, description: String, image: Uri?) -> Unit,
    onCloseClick: () -> Unit,
    sendRequestToUser: (nick: String) -> Unit = {},
    onUserLeaveGroup: (SmallUserModel) -> Unit = {}
) {
    var editMode by remember { mutableStateOf(false) }
    var membersView by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf(groupData.name) }
    var editedGroupName by remember { mutableStateOf(groupData.name) }
    var groupDesc by remember { mutableStateOf(groupData.desc) }
    var editedGroupDesc by remember { mutableStateOf(groupData.desc) }
    var nameValidation by remember { mutableStateOf(ValidationResult()) }
    var descValidation by remember { mutableStateOf(ValidationResult()) }
    val isOwner by remember { mutableStateOf(userId == groupData.ownerId) }
    var showInputDialog by remember{ mutableStateOf(false) }
    var groupImage by remember { mutableStateOf<Any?>(groupData.imageUrl) }
    var confirmLeaveDialog by remember { mutableStateOf(false) }
    var confirmRemoveUserDialog by remember { mutableStateOf(false) }
    var userToRemoveFromGroup by remember { mutableStateOf<SmallUserModel?>(null) }

    var selectedImage by remember { mutableStateOf<Any?>(Uri.EMPTY) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImage = uri
        }

    fun editModeChange() {
        editMode = !editMode
        nameValidation = nameValidation.copy(isError = false)
        descValidation = descValidation.copy(isError = false)

        selectedImage = groupImage
        editedGroupName = groupName
        editedGroupDesc = groupDesc
    }

    fun saveChanges(){
        nameValidation = if (editedGroupName.isEmpty()) {
            nameValidation.copy(
                isError = true,
                errorMessage = "This field can\'t be empty"
            )
        } else {
            nameValidation.copy(isError = false, errorMessage = "")
        }

        descValidation = if (editedGroupDesc.isEmpty()) {
            descValidation.copy(
                isError = true,
                errorMessage = "This field can't be empty"
            )
        } else {
            descValidation.copy(isError = false, errorMessage = "")
        }

        if (!nameValidation.isError && !descValidation.isError) {
            if(selectedImage != null){
                groupImage = selectedImage
            }
            groupName = editedGroupName
            groupDesc = editedGroupDesc
            onSaveClick(editedGroupName, editedGroupDesc, selectedImage as Uri?)
            editModeChange()
        }
    }

    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
        onDismissRequest = {
            onCloseClick()
        },
        title = {
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(modifier = Modifier
                    .clipToBounds()
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    if (!editMode && isOwner && !membersView) {
                        IconButton(onClick = {
                            editModeChange()
                        }, modifier = Modifier.align(Alignment.CenterEnd)) {
                            Icon(
                                Icons.Default.Edit,
                                stringResource(id = R.string.edit_group_details)
                            )
                        }
                    }
                    if(membersView && isOwner){
                        IconButton(onClick = {
                            showInputDialog = true
                        }, modifier = Modifier.align(Alignment.CenterEnd)) {
                            Icon(
                                Icons.Default.Add,
                                stringResource(id = R.string.add_member)
                            )
                        }
                    }
                    if(!editMode){
                        Button(onClick = {
                            membersView = !membersView
                        }) {
                            Text(
                                if (membersView) stringResource(id = R.string.group_details) else stringResource(
                                    id = R.string.members
                                )
                            )
                        }
                    }
                }

                Text("", modifier = Modifier.height(0.dp))

                if (membersView) {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items = groupData.membersList.values.toList(),
                            key = { member -> member.id },
                            contentType = { TaskModel::class.java }
                        )
                        { member ->
                            MemberCard(
                                memberData = member,
                                currentUserId = userId,
                                groupOwnerId = groupData.ownerId,
                                groupMemberCount = groupData.membersList.size,
                                onLeaveGroup = {
                                    userToRemoveFromGroup = it
                                    confirmLeaveDialog = true
                                },
                                onRemoveUser = {
                                    userToRemoveFromGroup = it
                                    confirmRemoveUserDialog = true
                                }
                            )
                        }
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(if(editMode) selectedImage else groupImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = "group Image",
                        error = rememberVectorPainter(Icons.Default.PhotoCamera),
                        placeholder = rememberVectorPainter(Icons.Default.PhotoCamera),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(80.dp, 80.dp)
                            .background(CustomThemeManager.colors.appBackground)
                            .clickable {
                                if (editMode) {
                                    galleryLauncher.launch("image/*")
                                }
                            }
                    )
                    InputField(
                        label = stringResource(id = R.string.group_name),
                        onTextChange = {
                            editedGroupName = it
                        },
                        validationResult = nameValidation,
                        enabled = editMode,
                        text = if(editMode) editedGroupName else groupName,
                    )
                    InputField(
                        label = stringResource(id = R.string.group_description),
                        onTextChange = {
                            editedGroupDesc = it
                        },
                        validationResult = descValidation,
                        enabled = editMode,
                        text = if(editMode) editedGroupDesc else groupDesc,
                    )
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (editMode) {
                    Button(
                        onClick = {
                            editModeChange()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.error
                        ),
                        modifier = Modifier
                            .padding(all = 8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = CustomThemeManager.colors.primaryColor
                        ),
                        onClick = {
                            saveChanges()
                        }
                    ) {
                        Text(
                            stringResource(id = R.string.save),
                            color = CustomThemeManager.colors.textColorOnPrimary
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            onCloseClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        ),
                        modifier = Modifier
                            .padding(all = 8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.close))
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    if(showInputDialog){
        InputFieldDialog(
            text = "Input user nick to send him request to join your group",
            onOkButtonClick = {
                sendRequestToUser(it)
                showInputDialog = !showInputDialog
            },
        onCancelButtonClick = {showInputDialog = !showInputDialog})
    }

    if(confirmLeaveDialog){
        InfoDialog(
            title = stringResource(id = R.string.leave_group),
            infoText = if(groupData.membersList.size == 1) stringResource(id = R.string.leave_group_text_last, groupData.name) else stringResource(id = R.string.leave_group_text, groupData.name),
            isDecisionDialog = true,
            onConfirm = {
                onUserLeaveGroup(userToRemoveFromGroup!!)
                userToRemoveFromGroup = null
                confirmLeaveDialog = false
            },
            onCancel = {
                userToRemoveFromGroup = null
                confirmLeaveDialog = false
            }
        )
    }

    if(confirmRemoveUserDialog){
        InfoDialog(
            title = stringResource(id = R.string.remove_from_group),
            infoText = stringResource(id = R.string.remove_from_group_text, userToRemoveFromGroup!!.displayName, groupData.name),
            isDecisionDialog = true,
            onConfirm = {
                onUserLeaveGroup(userToRemoveFromGroup!!)
                userToRemoveFromGroup = null
                confirmRemoveUserDialog = false
            },
            onCancel = {
                userToRemoveFromGroup = null
                confirmRemoveUserDialog = false
            }
        )
    }
}


@Preview
@Composable
fun EditGroupDialog_Preview() {
    EditGroupDialog(
        GroupModel(
            "asd",
            "test group",
            "asdasddsf  sdf sd",
            "asdasd",
            "asdsdfsdf",
            mapOf(
                "asdasd" to SmallUserModel("asdasd", "test", null),
                "asdasd" to SmallUserModel("asdasd", "test", null),
                "asdasd" to SmallUserModel("asdasd", "test", null),
                "asdasd" to SmallUserModel("asdasd", "test", null),
                "asdasd" to SmallUserModel("asdasd", "test", null),
                "asdasd" to SmallUserModel("asdasd", "test", null),
                "asdasd" to SmallUserModel("asdasd", "test", null),
            )
        ),
        "asdasd",
        onSaveClick = { _, _, _ -> },
        onCloseClick = {}
    )
}