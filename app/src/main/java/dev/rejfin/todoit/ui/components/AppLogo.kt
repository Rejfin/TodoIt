package dev.rejfin.todoit.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme

@Composable
fun AppLogo(modifier: Modifier = Modifier){
    val offset = Offset(0.0f, 1.0f)
    Text("TodoIt",
        style = TextStyle(
            fontSize = 32.sp,
            shadow = Shadow(
                color = Color.Gray,
                offset = offset,
                blurRadius = 3f
            )
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    CustomJetpackComposeTheme() {
        AppLogo()
    }
}