package dev.rejfin.todoit.models

data class TrophyModel(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val unlocked: Boolean = false
)
