package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.viewmodels.GroupsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.TaskModel
import dev.rejfin.todoit.ui.components.GroupListEntry
import dev.rejfin.todoit.ui.dialogs.CustomDialog
import dev.rejfin.todoit.ui.dialogs.DialogType
import dev.rejfin.todoit.ui.dialogs.GroupCreationDialog
import dev.rejfin.todoit.ui.screens.destinations.GroupDetailsScreenDestination
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Destination
@Composable
fun GroupsScreen(navigator: DestinationsNavigator?, viewModel: GroupsViewModel = viewModel()){
    var creationGroupDialogOpen by remember{ mutableStateOf(false)}

    Box(Modifier.fillMaxSize().background(CustomThemeManager.colors.appBackground)) {
        if(viewModel.groupList.isEmpty()){
            if(viewModel.isLoadingData){
                LinearProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }else{
                Text(
                    text = stringResource(id = R.string.none_group),
                    color = CustomThemeManager.colors.textColorSecond,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CustomThemeManager.colors.appBackground),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ){
                items(items = viewModel.groupList,
                    key = {group -> group.id },
                    contentType = { TaskModel::class.java }
                )
                {group ->
                    GroupListEntry(
                        groupModel = group,
                        modifier = Modifier.clickable {
                            navigator?.navigate(GroupDetailsScreenDestination(groupId = group.id))
                        }
                    )
                }
            }
        }
        if(creationGroupDialogOpen){
            GroupCreationDialog(
                onCancelClick = {
                    creationGroupDialogOpen = false
                },
                onCreateClick = {name, description, image ->
                    viewModel.createNewGroup(name, description, image)
                    creationGroupDialogOpen = false
                })
        }
        FloatingActionButton(
            onClick = {
                creationGroupDialogOpen = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            backgroundColor = CustomThemeManager.colors.secondaryColor){
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.new_task),
                tint = CustomThemeManager.colors.textColorOnPrimary
            )
        }
    }

    if(viewModel.errorState != null){
        CustomDialog(
            dialogType = DialogType.ERROR,
            message = viewModel.errorState!!,
            onConfirmClick = {
                viewModel.clearError()
            }
        )
    }

}

@Preview
@Composable
fun GroupsScreenPreview(){
    GroupsScreen(navigator = null)
}