package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rejfin.todoit.utils.CalendarUtility
import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.CustomDateFormat
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun Calendar(listOfDays: List<CalendarDay>, onDayClick:(clickedDayDate: CustomDateFormat)-> Unit, modifier: Modifier = Modifier) {

    val calendarUtility by remember {mutableStateOf(CalendarUtility()) }
    var selectedDay by remember{ mutableStateOf(calendarUtility.getCurrentDate()) }

    Row(
        modifier = modifier
    ) {
        listOfDays.forEach{calendarDay ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .clickable {
                        onDayClick(calendarDay.date)
                        selectedDay = calendarDay.date
                    }
            ) {
                Text(text = calendarDay.dayName,
                    color= CustomThemeManager.colors.textColorThird,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
                if(calendarUtility.areDateSame(calendarDay.date, selectedDay)){
                    Box(contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(90.dp))
                        .background(CustomThemeManager.colors.primaryColor)
                        .width(35.dp)
                        .height(35.dp)
                    ){
                        Text(text = calendarDay.date.day.toString(),
                            color = CustomThemeManager.colors.textColorOnPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W600
                        )
                    }
                }else{
                    Box(contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(90.dp))
                        .width(35.dp)
                        .height(35.dp)
                    ){
                        Text(text = calendarDay.date.day.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W600,
                            color = CustomThemeManager.colors.textColorFirst
                        )
                    }
                }


                if(calendarDay.numberOfTasks > 0 && calendarUtility.areDateSame(calendarUtility.getCurrentDate(), calendarDay.date)){
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp, bottom = 4.dp)
                            .clip(RoundedCornerShape(90.dp))
                            .background(CustomThemeManager.colors.primaryColor)
                            .width(8.dp)
                            .height(8.dp)
                    ){}
                }else{
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCalendar() {
    val calendarUtility = CalendarUtility()
    Calendar(calendarUtility.getDaysInCurrentWeek(), onDayClick = {println(it)}, modifier = Modifier
        .fillMaxWidth()
        .background(Color.White))
}