package dev.rejfin.todoit.models

data class UserModel(
    val id: String = "",
    val nick: String = "",
    val displayName: String = "",
    val imageUrl: String? = null,
    val taskDone: Int = 0,
    val allTask: Int = 0,
    val xp: Long = 0L,
    val groups: Map<String, Map<String, String>> = emptyMap()
)
