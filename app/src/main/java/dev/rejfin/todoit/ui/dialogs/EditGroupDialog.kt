package dev.rejfin.todoit.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.GroupModel
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.models.SmallUserModel
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.CustomImage
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.components.MemberCard
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun EditGroupDialog(
    groupData: GroupModel,
    membersList: Map<String, SmallUserModel>,
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
    var showInputDialog by remember { mutableStateOf(false) }
    var groupImage by remember { mutableStateOf<Any?>(groupData.imageUrl) }
    var confirmLeaveDialog by remember { mutableStateOf(false) }
    var confirmRemoveUserDialog by remember { mutableStateOf(false) }
    var userToRemoveFromGroup by remember { mutableStateOf<SmallUserModel?>(null) }

    var selectedImage by remember { mutableStateOf<Any?>(groupData.imageUrl) }

    fun onDataChanged(){
        editMode = (editedGroupName != groupName || editedGroupDesc != groupDesc || groupImage != selectedImage)
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if(uri != null){
                selectedImage = uri
                onDataChanged()
            }
        }

    fun editModeChange() {
        editMode = !editMode
        nameValidation = nameValidation.copy(isError = false)
        descValidation = descValidation.copy(isError = false)

        selectedImage = groupImage
        editedGroupName = groupName
        editedGroupDesc = groupDesc
    }

    fun saveChanges() {
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
            if (selectedImage != null) {
                groupImage = selectedImage
            }
            groupName = editedGroupName
            groupDesc = editedGroupDesc

            if(selectedImage == groupImage){
                onSaveClick(editedGroupName, editedGroupDesc, null)
            }else{
                onSaveClick(editedGroupName, editedGroupDesc, selectedImage as Uri?)
            }


            editModeChange()
        }
    }

    BaseDialog(
        dialogType = DialogType.INFO,
        dismissOnClickOutside = !isOwner,
        onDismissRequest = { onCloseClick() },
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                //Tab
                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                membersView = !membersView
                                editModeChange()
                            }
                    ) {
                        Text(
                            text = stringResource(id = R.string.about),
                            color = if(!membersView) CustomThemeManager.colors.primaryColor else CustomThemeManager.colors.textColorFirst.copy(alpha = 0.8f),
                            fontWeight = if(!membersView) FontWeight.W500 else FontWeight.W400,
                            modifier = Modifier
                                .padding(6.dp)
                        )
                        Spacer(
                            modifier = Modifier
                                .width(75.dp)
                                .background(CustomThemeManager.colors.primaryColor.copy(alpha = 0.9f))
                                .height(
                                    if (!membersView) {
                                        2.dp
                                    } else {
                                        0.dp
                                    }
                                )
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable {
                                membersView = !membersView
                                editModeChange()
                            }
                    ){
                        Text(
                            text = stringResource(id = R.string.members),
                            color = if(membersView) CustomThemeManager.colors.primaryColor else CustomThemeManager.colors.textColorFirst.copy(alpha = 0.8f),
                            fontWeight = if(membersView) FontWeight.W500 else FontWeight.W400,
                            modifier = Modifier
                                .padding(6.dp)
                        )
                        Spacer(
                            modifier = Modifier
                                .width(75.dp)
                                .background(CustomThemeManager.colors.primaryColor.copy(alpha = 0.7f))
                                .height(
                                    if (membersView) {
                                        2.dp
                                    } else {
                                        0.dp
                                    }
                                )
                        )
                    }
                }

                //Content
                if(!membersView){
                    CustomImage(
                        imageUrl = if (editMode) selectedImage?.toString() else groupImage?.toString(),
                        contentDescription = stringResource(id = R.string.group_image),
                        size = DpSize(100.dp, 100.dp),
                        placeholder = rememberVectorPainter(Icons.Default.PhotoCamera),
                        backgroundColor = CustomThemeManager.colors.appBackground,
                        editable = isOwner,
                        onEditClick = {
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 8.dp)
                    )
                    InputField(
                        label = stringResource(id = R.string.group_name),
                        onTextChange = {
                            editedGroupName = it
                            onDataChanged()
                        },
                        enabled = isOwner,
                        validationResult = nameValidation,
                        text = if (editMode) editedGroupName else groupName,
                    )
                    InputField(
                        label = stringResource(id = R.string.group_description),
                        onTextChange = {
                            editedGroupDesc = it
                            onDataChanged()
                        },
                        enabled = isOwner,
                        validationResult = descValidation,
                        text = if (editMode) editedGroupDesc else groupDesc,
                    )
                }else{
                    if(isOwner){
                        Button(
                            onClick = { showInputDialog = true },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(stringResource(id = R.string.add_member))
                        } 
                    }
                    
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items = membersList.values.toList(),
                            key = { member -> member.id },
                            contentType = { TaskModel::class.java }
                        )
                        { member ->
                            MemberCard(
                                memberData = member,
                                currentUserId = userId,
                                groupOwnerId = groupData.ownerId,
                                groupMemberCount = membersList.size,
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
                }


                //Buttons
                if(!membersView){
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                    ) {
                        if(editMode){
                            TextButton(onClick = {
                                onCloseClick()
                            }, modifier = Modifier
                                .background(
                                    CustomThemeManager.colors.errorColor.copy(
                                        if (CustomThemeManager.isSystemDarkTheme()) {
                                            0.65f
                                        } else {
                                            0.35f
                                        }
                                    )
                                )
                                .weight(1f)
                            ) {
                                Text(
                                    stringResource(id = R.string.cancel),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if(CustomThemeManager.isSystemDarkTheme()){
                                        CustomThemeManager.colors.textColorOnPrimary
                                    }else{
                                        CustomThemeManager.colors.errorColor
                                    },
                                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                                )
                            }
                        }

                        TextButton(onClick = {
                            if(editMode){
                                saveChanges()
                            }else{
                                onCloseClick()
                            }
                        }, modifier = Modifier
                            .background(
                                CustomThemeManager.colors.primaryColor.copy(
                                    if (CustomThemeManager.isSystemDarkTheme()) {
                                        0.65f
                                    } else {
                                        0.35f
                                    }
                                )
                            )
                            .weight(1f)
                        ) {
                            Text(
                                text = if(editMode) stringResource(id = R.string.save) else stringResource(id = R.string.ok),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if(CustomThemeManager.isSystemDarkTheme()){
                                    CustomThemeManager.colors.textColorOnPrimary
                                }else{
                                    CustomThemeManager.colors.primaryColor
                                },
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    )

    if (showInputDialog) {
        InputDialog(
            message = "Input user nick to send him request to join your group",
            errorMessage = stringResource(id = R.string.empty_field_error),
            allowedRegex = Regex("[^\\s]*"),
            onConfirmClick = {
                sendRequestToUser(it)
                showInputDialog = !showInputDialog
            },
            onCancelClick = {
                showInputDialog = !showInputDialog
            }
        )
    }

    if (confirmLeaveDialog) {
        CustomDialog(
            dialogType = DialogType.DECISION,
            title = stringResource(id = R.string.leave_group),
            message = if (membersList.size == 1) stringResource(
                id = R.string.leave_group_text_last,
                groupData.name
            ) else stringResource(id = R.string.leave_group_text, groupData.name),
            onCancelClick = {
                userToRemoveFromGroup = null
                confirmLeaveDialog = false
            },
            onConfirmClick = {
                onUserLeaveGroup(userToRemoveFromGroup!!)
                userToRemoveFromGroup = null
                confirmLeaveDialog = false
            }
        )
    }

    if (confirmRemoveUserDialog) {
        CustomDialog(
            dialogType = DialogType.INFO,
            title = stringResource(id = R.string.remove_from_group),
            message = stringResource(
                id = R.string.remove_from_group_text,
                userToRemoveFromGroup!!.displayName,
                groupData.name
            ),
            onCancelClick = {
                userToRemoveFromGroup = null
                confirmRemoveUserDialog = false
            },
            onConfirmClick = {
                onUserLeaveGroup(userToRemoveFromGroup!!)
                userToRemoveFromGroup = null
                confirmRemoveUserDialog = false
            }
        )
    }
}


@Preview
@Composable
fun EditGroupDialog_Preview() {
    CustomJetpackComposeTheme {
        EditGroupDialog(
            GroupModel(
                "asd",
                "test group",
                "asdasddsf  sdf sd",
                "asdasd",
                "asdsdfsdf",
                mutableMapOf(
                    "asdasd" to SmallUserModel("asdasd", "test", null),
                    "asdasd" to SmallUserModel("asdasd", "test", null),
                    "asdasd" to SmallUserModel("asdasd", "test", null),
                    "asdasd" to SmallUserModel("asdasd", "test", null),
                    "asdasd" to SmallUserModel("asdasd", "test", null),
                    "asdasd" to SmallUserModel("asdasd", "test", null),
                    "asdasd" to SmallUserModel("asdasd", "test", null),
                )
            ),
            mutableMapOf(),
            "asdasd",
            onSaveClick = { _, _, _ -> },
            onCloseClick = {}
        )
    }
}