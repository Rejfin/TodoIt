package dev.rejfin.todoit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.R
import dev.rejfin.todoit.components.Calendar
import dev.rejfin.todoit.ui.theme.Background
import dev.rejfin.todoit.ui.theme.PrimaryColor

@Destination
@Composable
fun HomeScreen(navigator: DestinationsNavigator?){
    Column(horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier
            .fillMaxSize()
            .background(Background)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
        ) {
            Text(text = "Witaj Jan Kowalski", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp))
            Text(text = "Wykonanych zadań na dziś 1/4", color= Color.Gray, modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp))
        }
        Calendar(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 14.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    HomeScreen(navigator = null)
}