package dev.rejfin.todoit.models

data class TaskModel(
    val id: String = "",
    val title: String = "test title",
    val description: String = "test description",
    val taskParts: List<Pair<Boolean, String>> = List(1) { Pair(false, "test") },
    val xpForTask: Int = 150,
    val isAllDay: Boolean = true,
    val timestampStart: Long? = null,
    val timestampStop: Long? = null
)
