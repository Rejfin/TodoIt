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
import androidx.compose.ui.graphics.Color
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
import dev.rejfin.todoit.models.UserModel
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.components.MemberCard
import dev.rejfin.todoit.ui.components.TaskCard
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun EditGroupDialog(
    groupData: GroupModel,
    userId: String,
    onCreateClick: (name: String, description: String, image: Uri) -> Unit,
    onCancelClick: () -> Unit,
    onCloseClick: () -> Unit,
    sendRequestToUser: (nick: String) -> Unit = {}
) {
    var editMode by remember { mutableStateOf(false) }
    var membersView by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf(groupData.name) }
    var groupDesc by remember { mutableStateOf(groupData.desc) }
    var nameValidation by remember { mutableStateOf(ValidationResult()) }
    var descValidation by remember { mutableStateOf(ValidationResult()) }
    val isOwner by remember { mutableStateOf(userId == groupData.ownerId) }
    var showInputDialog by remember{ mutableStateOf(false)}

    var selectedImage by remember { mutableStateOf(Uri.EMPTY) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImage = uri
        }

    fun editModeChange() {
        editMode = !editMode
        if (!editMode) {
            groupName = groupData.name
            groupDesc = groupData.desc
        }
    }

    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
        onDismissRequest = {
            onCancelClick()
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
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        items(items = groupData.membersList,
                            key = { member -> member.id },
                            contentType = { TaskModel::class.java }
                        )
                        { member ->
                            MemberCard(memberData = member, isOwner = member.id == userId)
                        }
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(groupData.imageUrl)
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
                                galleryLauncher.launch("image/*")
                            }
                    )
                    InputField(
                        label = stringResource(id = R.string.group_name),
                        onTextChange = {
                            groupName = it
                        },
                        validationResult = nameValidation,
                        enabled = editMode,
                        placeholder = groupName,
                        rememberTextInternally = false
                    )
                    InputField(
                        label = stringResource(id = R.string.group_description),
                        onTextChange = {
                            groupDesc = it
                        },
                        validationResult = descValidation,
                        enabled = editMode,
                        placeholder = groupDesc,
                        rememberTextInternally = false
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
                            nameValidation = if (groupName.isEmpty()) {
                                nameValidation.copy(
                                    isError = true,
                                    errorMessage = "This field can\'t be empty"
                                )
                            } else {
                                nameValidation.copy(isError = false, errorMessage = "")
                            }

                            descValidation = if (groupDesc.isEmpty()) {
                                descValidation.copy(
                                    isError = true,
                                    errorMessage = "This field can't be empty"
                                )
                            } else {
                                descValidation.copy(isError = false, errorMessage = "")
                            }

                            if (!nameValidation.isError && !descValidation.isError) {
                                onCreateClick(groupName, groupDesc, selectedImage)
                            }
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
            listOf(
                UserModel("asdasd", "test", null),
                UserModel("asdasd", "test", null),
                UserModel("asdasd", "test", null),
                UserModel("asdasd", "test", null),
                UserModel("asdasd", "test", null),
                UserModel("asdasd", "test", null),
                UserModel("asdasd", "test", null),
            )
        ),
        "asdasd",
        onCreateClick = { name, desc, img -> },
        onCancelClick = {},
        onCloseClick = {}
    )
}