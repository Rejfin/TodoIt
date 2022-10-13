package dev.rejfin.todoit.components

import androidx.compose.foundation.background
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
import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.ui.theme.CustomThemeManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Calendar(modifier: Modifier = Modifier) {

    val cal: Calendar = Calendar.getInstance(Locale.UK)
    val mDays = remember { mutableStateListOf<CalendarDay>() }
    mDays.addAll(getDaysInCurrentWeek())
    val sdfName = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
    val sdfNumber = remember { SimpleDateFormat("dd", Locale.getDefault()) }

    Row(
        modifier = modifier
    ) {

        mDays.forEach{calendarDay ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = sdfName.format(calendarDay.timestamp),
                    color= CustomThemeManager.colors.textColorThird,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
                if(sdfNumber.format(calendarDay.timestamp) == sdfNumber.format(cal.time.time)){
                    Box(contentAlignment = Alignment.Center, modifier = Modifier
                        .clip(RoundedCornerShape(90.dp))
                        .background(CustomThemeManager.colors.primaryColor)
                        .width(35.dp)
                        .height(35.dp)
                    ){
                        Text(text = sdfNumber.format(calendarDay.timestamp),
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
                        Text(text = sdfNumber.format(calendarDay.timestamp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W600,
                            color = CustomThemeManager.colors.textColorFirst
                        )
                    }
                }


                if(calendarDay.numberOfTasks > 0){
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

private fun getDaysInCurrentWeek(): List<CalendarDay>{
    val cal: Calendar = Calendar.getInstance(Locale.UK)
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)


    val listOfDays = mutableListOf<CalendarDay>()
    for (i in 0..6) {
        listOfDays.add(CalendarDay(cal.time.time, i))
        cal.add(Calendar.DAY_OF_WEEK, 1)
    }
    return listOfDays
}

@Preview(showBackground = true)
@Composable
private fun PreviewCalendar() {
    Calendar(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White))
}