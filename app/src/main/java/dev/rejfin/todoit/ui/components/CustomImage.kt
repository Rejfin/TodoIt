package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.rejfin.todoit.R
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun CustomImage(
    imageUrl: String?,
    contentDescription: String,
    size: DpSize,
    placeholder: Painter,
    modifier: Modifier = Modifier,
    editable: Boolean = false,
    backgroundColor: Color = CustomThemeManager.colors.cardBackgroundColor.copy(alpha = 0.8f),
    imageResize: Dp = 25.dp
){
    var showCircleBackground by remember{ mutableStateOf(true) }
    
    Box(
        modifier = modifier
            .size(size)
    ) {
        if(showCircleBackground){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(backgroundColor)
            )
        }
        
        if(editable){
            Box(
                modifier = Modifier
                    .padding(end = 3.dp, bottom = 4.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .border(1.dp, CustomThemeManager.colors.textColorFirst.copy(0.5f), CircleShape)
                    .background(CustomThemeManager.colors.cardBackgroundColor)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.edit_photo),
                    tint = CustomThemeManager.colors.textColorFirst,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.Center)
                )
            }
        }
        
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = placeholder,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            error = placeholder,
            onSuccess = {
                showCircleBackground = false
            },
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.Center)
                .size(
                    if (showCircleBackground)
                        size.copy(size.width - imageResize, size.height - imageResize)
                    else
                        size
                )
        )
    }
}

@Composable
@Preview
fun CustomImagePreview(){
    CustomImage(
        imageUrl = "",
        contentDescription = "test",
        size = DpSize(120.dp, 120.dp),
        placeholder = rememberVectorPainter(image = Icons.Default.Group),
        modifier = Modifier.size(120.dp, 120.dp),
        editable = true
        )
}