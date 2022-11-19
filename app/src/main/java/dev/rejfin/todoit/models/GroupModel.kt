package dev.rejfin.todoit.models

import androidx.compose.runtime.mutableStateMapOf

data class GroupModel(
    val id: String = "",
    val name: String = "",
    val desc: String = "",
    val ownerId: String = "",
    val imageUrl: String? = null,
    val membersList: Map<String, SmallUserModel> = mapOf()
){
    val memList = mutableStateMapOf<String, SmallUserModel>()

    init {
        membersList.forEach {
            memList[it.key] = it.value
        }
    }
}