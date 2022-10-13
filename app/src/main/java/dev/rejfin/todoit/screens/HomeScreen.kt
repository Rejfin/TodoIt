package dev.rejfin.todoit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.HomeViewModel
import dev.rejfin.todoit.components.Calendar
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.rejfin.todoit.components.TaskCard
import dev.rejfin.todoit.models.TaskModel

@Destination
@Composable
fun HomeScreen(navigator: DestinationsNavigator?, viewModel: HomeViewModel = viewModel()){
    Column(horizontalAlignment = Alignment.CenterHorizontally, 
        modifier = Modifier
            .fillMaxSize()
            .background(CustomThemeManager.colors.appBackground)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
        ) {
            Text(text = "Witaj Jan Kowalski",
                fontSize = 18.sp,
                color = CustomThemeManager.colors.textColorFirst,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 0.dp)
            )
            Text(text = "Wykonanych zadań na dziś 1/4",
                color= CustomThemeManager.colors.textColorSecond,
                modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
            )
        }
        Calendar(
            viewModel.calendarDays,
            onDayClick = {},
            modifier = Modifier
            .fillMaxWidth()
            .background(CustomThemeManager.colors.cardBackgroundColor)
            .padding(horizontal = 8.dp, vertical = 14.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items(items = viewModel.taskList,
                key = {task -> task.id },
                contentType = { TaskModel::class.java }
            )
            {task ->
                TaskCard(task = task)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    HomeScreen(navigator = null)
}