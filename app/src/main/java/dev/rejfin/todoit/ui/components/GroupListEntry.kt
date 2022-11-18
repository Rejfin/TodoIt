package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.rejfin.todoit.models.GroupModel
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun GroupListEntry(groupModel: GroupModel, modifier: Modifier = Modifier){
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CustomThemeManager.colors.cardBackgroundColor)
            .padding(8.dp)

    ){
        CustomImage(
            imageUrl = groupModel.imageUrl,
            contentDescription = groupModel.name,
            size = DpSize(50.dp, 50.dp),
            placeholder = rememberVectorPainter(Icons.Filled.Group),
            backgroundColor = CustomThemeManager.colors.appBackground,
            imageResize = 12.dp
        )
        Column(verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(text = groupModel.name,
                color = CustomThemeManager.colors.textColorFirst,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = groupModel.desc,
                color = CustomThemeManager.colors.textColorSecond,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun GroupListEntryPreview(){
    GroupListEntry(
        GroupModel("asdasd","test group", "short description of group", "asdasd","https://cdn.dribbble.com/userupload/3158902/file/original-7c71bfa677e61dea61bc2acd59158d32.jpg")
    )
}