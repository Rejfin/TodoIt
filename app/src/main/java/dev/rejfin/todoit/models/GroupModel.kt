package dev.rejfin.todoit.models

data class GroupModel(
    val id: String = "",
    val name: String = "",
    val desc: String = "",
    val ownerId: String = "",
    val imageUrl: String? = null,
    val membersList: Map<String, UserModel> = emptyMap()
)