package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun SettingEntry(title:String, desc: String, modifier: Modifier = Modifier, icon: ImageVector? = null, isChecked: Boolean = false, onCheckChange: ((Boolean) -> Unit)? = null, useCheckbox:Boolean = true){
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(CustomThemeManager.colors.cardBackgroundColor)
    ) {
        if(icon != null){
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = CustomThemeManager.colors.primaryColor,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(25.dp, 25.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(0.9f)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = CustomThemeManager.colors.textColorFirst
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = CustomThemeManager.colors.textColorSecond,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = modifier.weight(1f))
        if(onCheckChange != null){
            if(useCheckbox){
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckChange,
                    modifier = Modifier
                        .padding(end = 16.dp)
                )
            }else{
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckChange,
                    modifier = Modifier
                        .padding(end = 16.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun SettingEntryPreview(){
    SettingEntry(
        title = "test title",
        desc = "test desc asd asd a sd as da sd a sd af df gds fg dfhg dfh dfh df hd fh dfh dfh df hd fh dfh df hd fh dfh ",
        onCheckChange = {},
        icon = Icons.Default.Settings,
        useCheckbox = true
    )
}