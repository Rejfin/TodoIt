package dev.rejfin.todoit.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dev.rejfin.todoit.utils.CalendarUtility
import dev.rejfin.todoit.models.CalendarDay
import dev.rejfin.todoit.models.TaskModel

class HomeViewModel : ViewModel() {
    val taskList = mutableStateListOf<TaskModel>()

    var calendarDays = mutableStateListOf<CalendarDay>()
        private set

    init {
        calendarDays.addAll(
            CalendarUtility().getDaysInCurrentWeek()
        )
        //TODO get data from firebase
        calendarDays[3].numberOfTasks = 2

        taskList.addAll(
            listOf(
                TaskModel(
                    id = "asd-asdf-fgdfg",
                    title = "Test 1",
                    description = "first description of test 1",
                    isAllDay = true,
                    timestampStart = null,
                    timestampStop = null,
                    taskParts = listOf(
                        Pair(false, "")
                    )
                ),
                TaskModel(
                    id = "as890d-asd456f-fgdfgsg",
                    isAllDay = true,
                    timestampStart = 1665507745L,
                    timestampStop = 1665511345L,
                    taskParts = listOf(
                        Pair(true, "part 1"),
                        Pair(false, "part 2"),
                        Pair(true, "part 3"),
                    )
                ),
                TaskModel(
                    id = "as89ghjk0d-asd4ghk56f-fgdfghjkgsg",
                    isAllDay = false,
                    timestampStart = 1665507745L,
                    timestampStop = 1665511345L,
                    taskParts = listOf(
                        Pair(true, "part 1"),
                        Pair(true, "part 2")
                    )
                ),
                TaskModel(
                    id = "as89ghjfghk0d-asd4ghk56f-fgdfghjkgsg",
                    isAllDay = false,
                    timestampStart = 1665507745L,
                    timestampStop = 1665511345L,
                    taskParts = listOf(
                        Pair(true, "part 1"),
                        Pair(true, "part 2"),
                        Pair(true, "part 3"),
                        Pair(false, "part 4"),
                    )
                )
            )
        )
    }
}