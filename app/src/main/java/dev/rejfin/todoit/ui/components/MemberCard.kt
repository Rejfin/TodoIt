package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rejfin.todoit.models.UserModel
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun MemberCard(memberData: UserModel, modifier: Modifier = Modifier, isOwner: Boolean = false){
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CustomThemeManager.colors.cardBackgroundColor)
            .padding(8.dp)

    ){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(memberData.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = rememberVectorPainter(Icons.Filled.Person),
            contentDescription = memberData.displayName,
            contentScale = ContentScale.Crop,
            error = rememberVectorPainter(Icons.Filled.Person),
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp, 50.dp)
        )
        Column(verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(text = memberData.displayName,
                color = CustomThemeManager.colors.textColorFirst,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if(isOwner){
                Text(text = "Owner of group",
                    color = CustomThemeManager.colors.textColorSecond,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun MemberCard_Preview(){
    CustomJetpackComposeTheme {
        MemberCard(
            UserModel(
            id = "Asdasd",
            displayName = "Jan Kowalski",
            imageUrl = null
        ), isOwner = true)
    }
}