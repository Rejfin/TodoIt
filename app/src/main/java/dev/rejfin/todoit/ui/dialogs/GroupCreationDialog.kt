package dev.rejfin.todoit.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.ValidationResult
import dev.rejfin.todoit.ui.components.CustomImage
import dev.rejfin.todoit.ui.components.InputField
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun GroupCreationDialog(onCreateClick: (name:String, description:String, image:Uri) -> Unit, onCancelClick: () -> Unit){

    var groupName by remember{ mutableStateOf("") }
    var groupDesc by remember{ mutableStateOf("") }
    var nameValidation by remember{ mutableStateOf(ValidationResult()) }
    var descValidation by remember{ mutableStateOf(ValidationResult()) }
    var selectedImage by remember { mutableStateOf(Uri.EMPTY) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImage = uri
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
            Text(text = stringResource(id = R.string.create_group_title),
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {

                Text("", modifier = Modifier.height(0.dp))

                CustomImage(
                    imageUrl = selectedImage.toString(),
                    contentDescription = stringResource(id = R.string.group_image),
                    size = DpSize(100.dp, 100.dp),
                    placeholder = rememberVectorPainter(Icons.Default.PhotoCamera),
                    backgroundColor = CustomThemeManager.colors.appBackground,
                    editable = true,
                    onEditClick = {
                        galleryLauncher.launch("image/*")
                    }
                )

                InputField(
                    label = stringResource(id = R.string.group_name),
                    text = groupName,
                    onTextChange = {
                        groupName = it
                    },
                    validationResult = nameValidation
                )

                InputField(
                    label = stringResource(id = R.string.group_description),
                    text = groupDesc,
                    onTextChange = {
                        groupDesc = it
                    },
                    validationResult = descValidation
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier.padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onCancelClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = CustomThemeManager.colors.errorColor,
                        contentColor = CustomThemeManager.colors.textColorOnPrimary
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
                        nameValidation = if(groupName.isEmpty()){
                            nameValidation.copy(isError = true, errorMessage = "This field can\'t be empty")
                        }else{
                            nameValidation.copy(isError = false, errorMessage = "")
                        }

                        descValidation = if(groupDesc.isEmpty()){
                            descValidation.copy(isError = true, errorMessage = "This field can't be empty")
                        }else{
                            descValidation.copy(isError = false, errorMessage = "")
                        }

                        if(!nameValidation.isError && !descValidation.isError){
                            onCreateClick(groupName, groupDesc, selectedImage)
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.create_group), color = CustomThemeManager.colors.textColorOnPrimary)
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun GroupCreationDialog_Preview() {
    GroupCreationDialog(onCreateClick = { name, desc , img-> }, onCancelClick = {})
}