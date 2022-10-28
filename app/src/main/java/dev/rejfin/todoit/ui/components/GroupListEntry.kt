package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(groupModel.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = rememberVectorPainter(Icons.Filled.Group),
            contentDescription = groupModel.name,
            contentScale = ContentScale.Crop,
            error = rememberVectorPainter(Icons.Filled.Group),
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp, 50.dp)
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
fun GroupListEntry_Preview(){
    GroupListEntry(
        GroupModel("asdasd","test group", "short description of group", "asdasd","https://cdn.dribbble.com/userupload/3158902/file/original-7c71bfa677e61dea61bc2acd59158d32.jpg")
    )
}