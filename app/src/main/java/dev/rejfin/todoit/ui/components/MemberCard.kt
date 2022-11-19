package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rejfin.todoit.R
import dev.rejfin.todoit.models.SmallUserModel
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun MemberCard(memberData: SmallUserModel,
               currentUserId: String,
               groupOwnerId: String,
               groupMemberCount: Int,
               modifier: Modifier = Modifier,
               onRemoveUser: (SmallUserModel) -> Unit = {},
               onLeaveGroup: (SmallUserModel) -> Unit = {}){
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
                .padding(horizontal = 8.dp)
        ) {
            Text(text = memberData.displayName,
                color = CustomThemeManager.colors.textColorFirst,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if(memberData.id == currentUserId){
                Text(text = if(memberData.id == groupOwnerId) "You, owner of group" else "You",
                    color = CustomThemeManager.colors.textColorSecond,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )
            } else if(memberData.id == groupOwnerId){
                Text(text = "Owner of group",
                    color = CustomThemeManager.colors.textColorSecond,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(Modifier.weight(1f))
        if(currentUserId == groupOwnerId && currentUserId != memberData.id){
            IconButton(onClick = {
                onRemoveUser(memberData)
            },
            modifier= Modifier
                .size(45.dp, 45.dp)
            ) {
                Icon(Icons.Default.Delete, stringResource(id = R.string.remove_member))
            }
        }else if(currentUserId == memberData.id && currentUserId != groupOwnerId || (currentUserId == groupOwnerId && groupMemberCount == 1)){
            IconButton(onClick = {
                onLeaveGroup(memberData)
            },
                modifier= Modifier
                    .size(45.dp, 45.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.Logout, stringResource(id = R.string.leave_group))
            }
        }
    }
}

@Preview
@Composable
fun MemberCard_Preview(){
    CustomJetpackComposeTheme {
        MemberCard(
            SmallUserModel(
            id = "Asdasd",
            displayName = "Jan Kowalski",
            imageUrl = null
        ), "Asdasd", "asdasd", 1)
    }
}