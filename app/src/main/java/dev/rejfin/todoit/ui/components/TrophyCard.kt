package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.models.TrophyModel
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun TrophyCard(trophyModel: TrophyModel, modifier: Modifier = Modifier){
    Row(verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(CustomThemeManager.colors.cardBackgroundColor)
        .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = trophyModel.title,
            tint = if(trophyModel.unlocked) Color(0xFFFFB950) else Color(0xFFCACACA),
            modifier = Modifier
            .clip(CircleShape)
            .size(50.dp, 50.dp))

        Column{
            Text(
                text = trophyModel.title,
                fontSize = 18.sp,
                color = if(trophyModel.unlocked) CustomThemeManager.colors.textColorFirst else CustomThemeManager.colors.textColorThird,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = trophyModel.description,
                fontSize = 14.sp,
                color = if(trophyModel.unlocked) CustomThemeManager.colors.textColorSecond else CustomThemeManager.colors.textColorThird,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun TrophyCardPreview(){
    CustomJetpackComposeTheme{
        TrophyCard(TrophyModel())
    }
}