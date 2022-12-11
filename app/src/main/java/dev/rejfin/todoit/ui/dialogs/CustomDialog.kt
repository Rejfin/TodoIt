package dev.rejfin.todoit.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.InvitationModel
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import dev.rejfin.todoit.utils.Selectable

@Composable
fun CustomDialog(
    dialogType: DialogType,
    title: String? = null,
    message: String? = null,
    dismissOnClickOutside: Boolean = false,
    selectableList: List<Selectable>? = null,
    textOnListEmpty: String = "tests et se tse tse t",
    onDismissClick: () -> Unit = {},
    onConfirmClick: (() -> Unit)? = null,
    onCancelClick: (() -> Unit)? = null,
    onEntryListClick: ((Selectable) -> Unit)? = null
){
    BaseDialog(
        dialogType = dialogType,
        dismissOnClickOutside = (onConfirmClick == null && onCancelClick == null) || dismissOnClickOutside,
        onDismissRequest = {
            onDismissClick()
        },
        content = {
            Column(modifier = Modifier.fillMaxWidth()){
                if(title != null){
                    Text(text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W500,
                        color = CustomThemeManager.colors.textColorFirst,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    )
                }

                if(message != null){
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        color = CustomThemeManager.colors.textColorFirst,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    )
                }

                if(selectableList != null){
                    if(selectableList.isEmpty()){
                        Text(
                            text = textOnListEmpty,
                            color = CustomThemeManager.colors.textColorFirst,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
                        )
                    }else{
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ){
                            itemsIndexed(
                                items = selectableList,
                                key = {_, entry -> entry.getObjectId() }
                            )
                            {index, entry ->
                                Text(
                                    text = stringResource(id = R.string.invitation_to, entry.getString()),
                                    color = CustomThemeManager.colors.textColorFirst,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (index % 2 == 0)
                                                CustomThemeManager.colors.textColorThird.copy(alpha = 0.2f)
                                            else
                                                CustomThemeManager.colors.textColorThird.copy(alpha = 0.35f)
                                        )
                                        .padding(vertical = 16.dp, horizontal = 8.dp)
                                        .clickable(enabled = onEntryListClick != null) {
                                            onEntryListClick!!(entry)
                                        }
                                )
                            }
                        }
                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                ) {
                    if(onCancelClick != null){
                        TextButton(onClick = {
                            onCancelClick()
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

                    if(onConfirmClick != null || onCancelClick != null){
                        TextButton(onClick = {
                            if(onConfirmClick != null){
                                onConfirmClick()
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
                                stringResource(id = R.string.ok),
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
}

@Composable
@Preview
fun CustomDialogPreview(){
    CustomJetpackComposeTheme(customTheme = CustomTheme.LIGHT) {
        CustomDialog(
            dialogType = DialogType.INFO,
            title = "test title asd asd as dasd asd",
            message = "asdasd a sd asd asd a bsgbjh ghb gjhbsdf",
            onConfirmClick = {},
            onCancelClick = {},
            selectableList = listOf(
                InvitationModel("asd", "testasdasd", "", "test invitation"),
                InvitationModel("a23sd",  "testasdasd", "null", "test invitation"),
                InvitationModel("asr4d",  "testasdasd", "null", "test invitation"),
                InvitationModel("as32d",  "testasdasd", "null", "test invitation")
            ),
            textOnListEmpty = "",
            onEntryListClick = {}
        )
    }
}